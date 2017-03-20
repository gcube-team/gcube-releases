package gr.uoa.di.madgik.execution.utils;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.datatype.DataTypeArray;
import gr.uoa.di.madgik.execution.datatype.DataTypeReflectable;
import gr.uoa.di.madgik.execution.datatype.IDataType;
import gr.uoa.di.madgik.execution.datatype.ReflectableItem;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ReflectableAnalyzer
{
	private static Logger logger = LoggerFactory.getLogger(ReflectableAnalyzer.class);
	public Class<?> Reflectable = null;
	public HashSet<String> UnderInvestigation = new HashSet<String>();
	public IExternalClassLoader extLoader = null;
	
	public ReflectableAnalyzer(){}

	public ReflectableAnalyzer(Class<?> Reflectable, IExternalClassLoader extLoader)
	{
		this.Reflectable = Reflectable;
		this.UnderInvestigation.add(this.Reflectable.getName());
		this.extLoader = extLoader;
	}

	private ReflectableAnalyzer(Class<?> Reflectable, HashSet<String> UnderInvestigation, IExternalClassLoader extLoader)
	{
		this.Reflectable = Reflectable;
		this.UnderInvestigation = UnderInvestigation;
		this.UnderInvestigation.add(this.Reflectable.getName());
		this.extLoader = extLoader;
	}

	public boolean CanDescribeAsReflectable()
	{
		Class<?> originalReflectable = this.Reflectable;
		if (this.Reflectable.isArray())
		{
			this.Reflectable = ReflectableAnalyzer.GetBaseComponentType(this.Reflectable, this.extLoader);
			this.UnderInvestigation.clear();
			this.UnderInvestigation.add(this.Reflectable.getName());
		}
		if (this.IsPrimitive(this.Reflectable) != null) return false;
		if (!this.GetterSettersExistForFields()) return false;
		this.Reflectable = originalReflectable;
		return true;
	}

	public boolean CanRepresentAsReflectable()
	{
		if (this.Reflectable.isArray()) { return false; }
		if (this.IsPrimitive(this.Reflectable) != null) { return false; }
		if (!this.GetterSettersExistForFields()) { return false; }
		return true;
	}

	public String GetReflectableDescription()
	{
		try
		{
			this.UnderInvestigation.clear();
			this.UnderInvestigation.add(this.Reflectable.getName());
			StringBuilder buf = new StringBuilder();
			Class<?> currentType = this.Reflectable;
			if (this.Reflectable.isArray())
			{
				int dims = DataTypeUtils.CountDimentionsOfObjectArrayCode(this.Reflectable.getName());
				String arrtype = "";
				for (int i = 0; i < dims; i += 1)
					arrtype += "[";
				arrtype += IDataType.DataTypes.Reflectable.toString();
				buf.append("<execprf:array type=\"" + arrtype + "\">");
				currentType = ReflectableAnalyzer.GetBaseComponentType(this.Reflectable, this.extLoader);
			}
			buf.append("<execprf:refl type=\"" + currentType.getName() + "\">");
			HashSet<ReflectableAnalyzerMemberInfo> infos = this.GetFirstLevelGettersSetters();
			for (ReflectableAnalyzerMemberInfo nfo : infos)
			{
				buf.append("<execprf:item>");
				buf.append("<execprf:name value=\"" + nfo.name + "\"/>");
				buf.append("<execprf:type value=\"" + nfo.type.getName() + "\"/>");
				IDataType.DataTypes dtype = this.IsDirectPrimitive(nfo.type);
				String engType = null;
				if (dtype == null) dtype = IDataType.DataTypes.Reflectable;
				engType = dtype.toString();
				if (nfo.type.isArray())
				{
					int dims = DataTypeUtils.CountDimentionsOfObjectArrayCode(nfo.type.getName());
					StringBuilder engTypeBuf=new StringBuilder(); 
					engType = "";
					for (int i = 0; i < dims; i += 1) engTypeBuf.append("[");
					engTypeBuf.append(dtype.toString());
					engType += engTypeBuf.toString();
				}
				buf.append("<execprf:engineType value=\"" + engType + "\">");
				if (dtype.equals(IDataType.DataTypes.Reflectable))
				{
					ReflectableAnalyzer anal = new ReflectableAnalyzer(nfo.type, this.extLoader);
					if (anal.CanDescribeAsReflectable()) buf.append(anal.GetReflectableDescription());
				}
				buf.append("</execprf:engineType>");
				buf.append("</execprf:item>");
			}
			buf.append("</execprf:refl>");
			if (this.Reflectable.isArray())
			{
				buf.append("</execprf:array>");
			}
			return buf.toString();
		} catch (ExecutionValidationException ex)
		{
			return null;
		}
	}
	
	/*
	 * <REFL>
	 * <item type="LITERAL" tokens="">
	 * payload
	 * </item>
	 * <item type="ARRAY">
	 * <item type="LITERAL">
	 * payload
	 * </item>
	 * </item>
	 * <item type="LITERAL">
	 * payload
	 * </item>
	 * </REFL>;
	 * 
	 */
	public String PopulateTemplateFromReflectable(String Template, DataTypeReflectable refl,Map<String,String> TokenMapping) throws Exception
	{
		logger.debug("Template is : "+Template);
		StringBuilder buf=new StringBuilder();
		Document doc=XMLUtils.Deserialize(Template);
		List<Element> templateItems=XMLUtils.GetChildElementsWithName(doc.getDocumentElement(), "item");
		logger.debug("Number of items in template is "+templateItems.size());
		for(Element templateElem : templateItems)
		{
			if(!XMLUtils.AttributeExists(templateElem, "type")) throw new ExecutionValidationException("Invalid template provided");
			if(XMLUtils.GetAttribute(templateElem, "type").equals("literal"))
			{
				String tokenSeparator=null;
				if(XMLUtils.AttributeExists(templateElem, "tokenSeparator"))
				{
					tokenSeparator=XMLUtils.GetAttribute(templateElem, "tokenSeparator");
				}
				String []tokens=new String[0];
				if(XMLUtils.AttributeExists(templateElem, "tokens"))
				{
					if(tokenSeparator==null) throw new ExecutionValidationException("Invalid template provided");
					String tokenStrings=XMLUtils.GetAttribute(templateElem, "tokens");
					tokens=tokenStrings.split(tokenSeparator);
				}
				boolean isXML=false;
				if(XMLUtils.AttributeExists(templateElem, "isxml") && DataTypeUtils.GetValueAsBoolean(XMLUtils.GetAttribute(templateElem, "isxml"))) isXML=true;
				String payload=null;
				if(!isXML) payload=XMLUtils.UndoReplaceSpecialCharachters(XMLUtils.GetChildText(templateElem));
				else payload=XMLUtils.SerializeChild(templateElem);
				if(payload==null || payload.trim().length()==0) continue;
				logger.debug("Number of tokens to replace "+tokens.length);
				for(String tok : tokens)
				{
					if(tok==null || tok.trim().length()==0) continue;
					for(ReflectableItem it : refl)
					{
						logger.debug("Comparing '"+this.GetToken(TokenMapping, tok.trim())+"' with '"+this.GetToken(TokenMapping, it.Token)+"'");
						if(this.GetToken(TokenMapping, it.Token).equalsIgnoreCase(this.GetToken(TokenMapping, tok.trim())))
						{
							logger.debug("Found match for tokens "+this.GetToken(TokenMapping, tok.trim())+" and inserting value "+it.Value.GetStringValue());
							logger.debug("Original message is "+payload);
							while(payload.contains(this.GetToken(TokenMapping, tok.trim())))
							{
								payload=payload.replace(this.GetToken(TokenMapping, tok.trim()), it.Value.GetStringValue());
							}
							logger.debug("Replaced message is "+payload);
						}
					}
				}
				buf.append(payload);
			}
			else if(XMLUtils.GetAttribute(templateElem, "type").equals("refl"))
			{
				if(!XMLUtils.AttributeExists(templateElem, "token")) throw new ExecutionValidationException("Invalid template provided");
				logger.debug("Processing template item with token "+this.GetToken(TokenMapping, XMLUtils.GetAttribute(templateElem, "token")));
				for(ReflectableItem it : refl)
				{
					logger.debug("Matching it with "+this.GetToken(TokenMapping, it.Token));
					if(this.GetToken(TokenMapping, it.Token).equalsIgnoreCase(this.GetToken(TokenMapping, XMLUtils.GetAttribute(templateElem, "token"))))
					{
						String subTemplate=XMLUtils.SerializeChild(templateElem);
						logger.debug("Matched and passing subtemplate "+subTemplate);
						logger.debug("Matched reflection item is "/*+it.Value.ToXML()*/);
						if(!(it.Value instanceof DataTypeReflectable)) throw new ExecutionValidationException("Value not of required type for template");
						ReflectableAnalyzer anal=new ReflectableAnalyzer();
						buf.append(anal.PopulateTemplateFromReflectable(subTemplate, (DataTypeReflectable)it.Value,TokenMapping));
						break;
					}
				}
			}
			else if(XMLUtils.GetAttribute(templateElem, "type").equals("array"))
			{
				if(!XMLUtils.AttributeExists(templateElem, "token")) throw new ExecutionValidationException("Invalid template provided");
				for(ReflectableItem it : refl)
				{
					logger.debug("Matching it with "+this.GetToken(TokenMapping, it.Token));
					if(this.GetToken(TokenMapping, it.Token).equalsIgnoreCase(this.GetToken(TokenMapping, XMLUtils.GetAttribute(templateElem, "token"))))
					{
						if(!(it.Value instanceof DataTypeArray)) throw new ExecutionValidationException("Value not of required type for template");
						Element arrayElem=XMLUtils.GetChildElementWithName(templateElem, "item");
						if(!XMLUtils.AttributeExists(arrayElem, "type")) throw new ExecutionValidationException("Invalid template provided");
						if(XMLUtils.GetAttribute(arrayElem, "type").equals("array")) throw new ExecutionValidationException("Invalid template provided");
						else if(XMLUtils.GetAttribute(arrayElem, "type").equals("literal"))
						{
							String tokenSeparator=null;
							if(XMLUtils.AttributeExists(arrayElem, "tokenSeparator"))
							{
								tokenSeparator=XMLUtils.GetAttribute(arrayElem, "tokenSeparator");
							}
							String []tokens=new String[0];
							if(XMLUtils.AttributeExists(arrayElem, "tokens"))
							{
								if(tokenSeparator==null) throw new ExecutionValidationException("Invalid template provided");
								String tokenStrings=XMLUtils.GetAttribute(arrayElem, "tokens");
								tokens=tokenStrings.split(tokenSeparator);
							}
							boolean isXML=false;
							if(XMLUtils.AttributeExists(arrayElem, "isxml") && DataTypeUtils.GetValueAsBoolean(XMLUtils.GetAttribute(arrayElem, "isxml"))) isXML=true;
							String originalPayload=null;
							if(!isXML) originalPayload=XMLUtils.UndoReplaceSpecialCharachters(XMLUtils.GetChildText(arrayElem));
							else originalPayload=XMLUtils.SerializeChild(arrayElem);
							if(originalPayload==null || originalPayload.trim().length()==0) continue;
							logger.debug("Number of tokens to replace "+tokens.length);
							for(IDataType dtArr : (DataTypeArray)it.Value)
							{
								String payload = new String(originalPayload);
								for(String tok : tokens)
								{
									if(tok==null || tok.trim().length()==0) continue;
									while(payload.contains(this.GetToken(TokenMapping, tok.trim())))
									{
										payload=payload.replace(this.GetToken(TokenMapping, tok.trim()), dtArr.GetStringValue());
									}
								}
								buf.append(payload);
							}
						}
						else if(XMLUtils.GetAttribute(arrayElem, "type").equals("refl"))
						{
							String subTemplate=XMLUtils.SerializeChild(arrayElem);
							logger.debug("Matched and passing subtemplate "+subTemplate);
							logger.debug("Matched reflection item is "/*+it.Value.ToXML()*/);
							for(IDataType dtArr : (DataTypeArray)it.Value)
							{
								if(!(dtArr instanceof DataTypeReflectable)) throw new ExecutionValidationException("Invalid template provided");
								ReflectableAnalyzer anal=new ReflectableAnalyzer();
								buf.append(anal.PopulateTemplateFromReflectable(subTemplate, (DataTypeReflectable)dtArr,TokenMapping));
							}
						}
						else
						{
							throw new ExecutionValidationException("invalid template provided");
						}
						break;
					}
				}
			}
			else
			{
				throw new ExecutionValidationException("invalid template provided");
			}
		}
		return buf.toString();
	}

	// <refl name="value" prefix="value"><item name="value" token="value"
	// prefix="value" type="DATATYPES"/> </REFL>
	public DataTypeReflectable PopulateReflectableFromTemplate(String Template, String Value,int i,Map<String,String> TokenMapping) throws Exception
	{
		logger.debug("Template input is = "+Template);
		logger.debug("Value input is = "+Value);
		logger.debug("Array index is = "+i);
		List<ReflectableItem> items = new ArrayList<ReflectableItem>();
		Document docValue = XMLUtils.Deserialize(Value);
		Document docTemplate = XMLUtils.Deserialize(Template);
		Element reflTemplateElement = docTemplate.getDocumentElement();
		if (reflTemplateElement == null) throw new ExecutionValidationException("invalid template provided");
		if (!reflTemplateElement.getNodeName().equals("refl")) throw new ExecutionValidationException("invalid template provided");
		if (!XMLUtils.AttributeExists(reflTemplateElement, "name")) throw new ExecutionValidationException("invalid template provided");
		if (!XMLUtils.AttributeExists(reflTemplateElement, "ns")) throw new ExecutionValidationException("invalid template provided");
		List<Element> reflValueElements = null;
		if(docValue.getDocumentElement().getLocalName().equals(XMLUtils.GetAttribute(reflTemplateElement, "name")) && docValue.getDocumentElement().getNamespaceURI().equals(XMLUtils.GetAttribute(reflTemplateElement, "ns")))
		{
			reflValueElements = new ArrayList<Element>();
			reflValueElements.add(docValue.getDocumentElement());
		}
		else
		{
			reflValueElements = XMLUtils.GetChildElementsWithNameAndNamespace(docValue.getDocumentElement(), XMLUtils.GetAttribute(reflTemplateElement, "name"), XMLUtils.GetAttribute(reflTemplateElement, "ns"));
		}
		if (reflValueElements == null || reflValueElements.size()==0) throw new ExecutionValidationException("invalid value for template provided");
		logger.debug("number of of values are "+reflValueElements.size());
		List<Element> itemsTemplateLst = XMLUtils.GetChildElementsWithName(reflTemplateElement, "item");
		for (Element ittempl : itemsTemplateLst)
		{
			if (!XMLUtils.AttributeExists(ittempl, "name") || !XMLUtils.AttributeExists(ittempl, "token") || !XMLUtils.AttributeExists(ittempl, "type") || !XMLUtils.AttributeExists(ittempl, "ns")) throw new ExecutionValidationException("invalid template provided");
			IDataType.DataTypes itemDataType = IDataType.DataTypes.valueOf(XMLUtils.GetAttribute(ittempl, "type"));
			logger.debug("item data type = "+itemDataType.toString());
			Element refValueItemToken = XMLUtils.GetChildElementWithNameAndNamespace(reflValueElements.get(i), XMLUtils.GetAttribute(ittempl, "token"), XMLUtils.GetAttribute(ittempl, "ns"));
			if (refValueItemToken == null) throw new ExecutionValidationException("invalid Value for template provided"); //there could also be a continue here
			IDataType dt = DataTypeUtils.GetDataType(itemDataType);
			switch (dt.GetDataTypeEnum())
			{
				case Array:
					throw new ExecutionValidationException("Embedded arrays are not supported");
				case Convertable:
					throw new ExecutionValidationException("Convertables are not supported");
				case Reflectable:
				{
					Element reflSubElement= XMLUtils.GetChildElementWithName(ittempl, "refl");
					if(reflSubElement==null) throw new ExecutionValidationException("invalid template provided");
					ReflectableAnalyzer anal=new ReflectableAnalyzer();
					String subElemTemplate=XMLUtils.Serialize(reflSubElement);
					String subElemValue=XMLUtils.Serialize(refValueItemToken);
					logger.debug("Passing to sub reflectable template "+subElemTemplate);
					logger.debug("Passing to sub reflectable value "+subElemValue);
					dt.SetValue(anal.PopulateReflectableFromTemplate(subElemTemplate, subElemValue, 0,TokenMapping));
					logger.debug("Created sub element "/*+dt.ToXML()*/);
					break;
				}
				case BooleanClass:
				case BooleanPrimitive:
				case DoubleClass:
				case DoublePrimitive:
				case FloatClass:
				case FloatPrimitive:
				case IntegerClass:
				case IntegerPrimitive:
				case String:
				case ResultSet:
				{
					dt.SetStringValue(XMLUtils.UndoReplaceSpecialCharachters(XMLUtils.GetChildText(refValueItemToken)));
					break;
				}
				default:
					throw new ExecutionValidationException("Unrecognized data type");
			}
			ReflectableItem newItem = new ReflectableItem();
			newItem.Name = this.GetToken(TokenMapping, XMLUtils.GetAttribute(ittempl, "name"));
			newItem.Token = "[execprfBeginToken]" + newItem.Name + "[execprfEndToken]";
			newItem.Value = dt;
			items.add(newItem);
		}
		List<Element> arraysTemplateLst = XMLUtils.GetChildElementsWithName(reflTemplateElement, "array");
		logger.debug("Found "+arraysTemplateLst.size()+" arrays references in template ");
		for (Element ittempl : arraysTemplateLst)
		{
			IDataType dt=null;
			Element reflSubElement= XMLUtils.GetChildElementWithName(ittempl, "refl");
			if(reflSubElement!=null)
			{
				dt=new DataTypeArray();
				((DataTypeArray)dt).SetArrayClassCode("["+IDataType.DataTypes.Reflectable);
				List<IDataType> arritems=new ArrayList<IDataType>();
				ReflectableAnalyzer anal=new ReflectableAnalyzer();
				String subElemTemplate=XMLUtils.Serialize(reflSubElement);
				String subElemValue=XMLUtils.Serialize(reflValueElements.get(i));
				List<Element> arritemselems=XMLUtils.GetChildElementsWithNameAndNamespace(reflValueElements.get(i), XMLUtils.GetAttribute(reflSubElement, "name"), XMLUtils.GetAttribute(reflSubElement, "ns"));
				for(int x=0;x<arritemselems.size();x+=1)
				{
					logger.debug("Passing to sub reflectable template "+subElemTemplate);
					logger.debug("Passing to sub reflectable value "+subElemValue);
					logger.debug("Passing to sub reflectable index "+x);
					arritems.add(anal.PopulateReflectableFromTemplate(subElemTemplate, subElemValue, x,TokenMapping));
					logger.debug("Created sub element "/*+dt.ToXML()*/);
				}
				dt.SetValue(arritems.toArray(new IDataType[0]));
			}
			else
			{
				Element primSubElement= XMLUtils.GetChildElementWithName(ittempl, "item"); //Only one item can be declared
				if(primSubElement==null) throw new ExecutionValidationException("invalid template provided");
				if (!XMLUtils.AttributeExists(primSubElement, "name") || !XMLUtils.AttributeExists(primSubElement, "token") || !XMLUtils.AttributeExists(primSubElement, "type") || !XMLUtils.AttributeExists(primSubElement, "ns")) throw new ExecutionValidationException("invalid template provided");
				IDataType.DataTypes itemSubDataType = IDataType.DataTypes.valueOf(XMLUtils.GetAttribute(primSubElement, "type"));
				dt=new DataTypeArray();
				((DataTypeArray)dt).SetArrayClassCode("["+itemSubDataType.toString()); //only single dimension arrays
				logger.debug("Searching for elements with name "+XMLUtils.GetAttribute(primSubElement, "token")+" and ns "+XMLUtils.GetAttribute(primSubElement, "ns")+" under value element with name "+reflValueElements.get(i).getLocalName());
				List<Element> refValueSubElements= XMLUtils.GetChildElementsWithNameAndNamespace(reflValueElements.get(i), XMLUtils.GetAttribute(primSubElement, "token"), XMLUtils.GetAttribute(primSubElement, "ns"));
				logger.debug("found "+refValueSubElements.size()+" items");
				if(refValueSubElements==null) throw new ExecutionValidationException("invalid values provided for template");
				List<IDataType> arritems=new ArrayList<IDataType>();
				for(Element refValueSubitem : refValueSubElements)
				{
					IDataType dtarritem=DataTypeUtils.GetDataType(itemSubDataType);
					dtarritem.SetStringValue(XMLUtils.UndoReplaceSpecialCharachters(XMLUtils.GetChildText(refValueSubitem)));
					arritems.add(dtarritem);
				}
				((DataTypeArray)dt).SetValue(arritems.toArray(new IDataType[0]));
			}
			ReflectableItem newItem = new ReflectableItem();
			newItem.Name = this.GetToken(TokenMapping, XMLUtils.GetAttribute(ittempl, "name"));
			newItem.Token = "[execprfBeginToken]" + newItem.Name + "[execprfEndToken]";
			newItem.Value = dt;
			items.add(newItem);
		}
		DataTypeReflectable refl = new DataTypeReflectable();
		refl.SetValue(items.toArray(new ReflectableItem[0]));
		return refl;
	}
	
	private String GetToken(Map<String,String> TokenMapping, String Token)
	{
		if(TokenMapping==null) return Token;
		if(TokenMapping.containsKey(Token)) return TokenMapping.get(Token);
		return Token;
	}

	public Object PopulateTarget(DataTypeReflectable template,Map<String,String> TokenMapping) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ExecutionValidationException
	{
		if (this.Reflectable.isArray()) return null;
		this.UnderInvestigation.clear();
		this.UnderInvestigation.add(this.Reflectable.getName());
		Object target = this.Reflectable.newInstance();
		HashSet<ReflectableAnalyzerMemberInfo> firstLevel = this.GetFirstLevelGettersSetters();
		for (ReflectableItem item : template)
		{
			for (ReflectableAnalyzerMemberInfo nfo : firstLevel)
			{
				IDataType.DataTypes dtype = this.IsDirectPrimitive(nfo.type);
				if (dtype != null)
				{
					if (!this.GetToken(TokenMapping, nfo.name).equals(this.GetToken(TokenMapping, item.Name))) continue;
					if (!nfo.type.isArray())
					{
						if (!dtype.equals(item.Value.GetDataTypeEnum())) continue;
					}
					else
					{
						if (!item.Value.GetDataTypeEnum().equals(IDataType.DataTypes.Array)) continue;
						if (!(item.Value instanceof DataTypeArray)) continue;
						if (!dtype.equals(DataTypeUtils.GetComponentDataTypeOfArrayInitializingCode(((DataTypeArray) item.Value).GetArrayClassCode()))) continue;
						int dim = DataTypeUtils.CountDimentionsOfObjectArrayCode(((DataTypeArray) item.Value).GetArrayClassCode());
						if (dim != DataTypeUtils.CountDimentionsOfObjectArrayCode(nfo.type.getName())) continue;
					}
					Object val = item.Value.GetValue();
					nfo.setter.invoke(target, val);
				}
				else
				{
					if (!this.GetToken(TokenMapping, nfo.name).equals(this.GetToken(TokenMapping, item.Name))) continue;
					if (!nfo.type.isArray())
					{
						ReflectableAnalyzer anal = new ReflectableAnalyzer(nfo.type, this.extLoader);
						if (!anal.CanRepresentAsReflectable()) continue;
						if (!item.Value.GetDataTypeEnum().equals(IDataType.DataTypes.Reflectable)) continue;
						if (!(item.Value instanceof DataTypeReflectable)) continue;
						Object subValue = anal.PopulateTarget((DataTypeReflectable) item.Value,TokenMapping);
						nfo.setter.invoke(target, subValue);
					}
					else
					{
						if (!item.Value.GetDataTypeEnum().equals(IDataType.DataTypes.Array)) continue;
						if (!(item.Value instanceof DataTypeArray)) continue;
						int dim = DataTypeUtils.CountDimentionsOfObjectArrayCode(((DataTypeArray) item.Value).GetArrayClassCode());
						if (dim != DataTypeUtils.CountDimentionsOfObjectArrayCode(nfo.type.getName())) continue;
						if (!DataTypeUtils.GetComponentDataTypeOfArrayInitializingCode(((DataTypeArray) item.Value).GetArrayClassCode()).equals(IDataType.DataTypes.Reflectable)) continue;
						Class<?> compType = ReflectableAnalyzer.GetBaseComponentType(nfo.type, this.extLoader);
						if (compType == null) continue;
						Object arrTempl = ((DataTypeArray) item.Value).GetValue();
						int[] dims = new int[dim];
						dims[0] = Array.getLength(arrTempl);
						Object arrInst = this.CopyArray(arrTempl, compType,TokenMapping);
						if (arrInst == null) continue;
						nfo.setter.invoke(target, arrInst);
					}
				}
			}
		}
		return target;
	}

	private Object CopyArray(Object source, Class<?> compType,Map<String,String> TokenMapping) throws ArrayIndexOutOfBoundsException, IllegalArgumentException, ExecutionValidationException, InstantiationException, IllegalAccessException, InvocationTargetException
	{
		if (!source.getClass().isArray()) return null;
		Object arr = null;
		if (source.getClass().getComponentType().isArray())
		{
			int[] dims = new int[DataTypeUtils.CountDimentionsOfObjectArrayCode(source.getClass().getName())];
			dims[0] = Array.getLength(source);
			arr = Array.newInstance(compType, dims);
			for (int i = 0; i < Array.getLength(source); i += 1)
			{
				Array.set(arr, i, CopyArray(Array.get(source, i), compType,TokenMapping));
			}
		}
		else
		{
			arr = Array.newInstance(compType, Array.getLength(source));
			for (int i = 0; i < Array.getLength(source); i += 1)
			{
				Object subValue = null;
				Object initValue = Array.get(source, i);
				if (initValue != null && (initValue instanceof DataTypeReflectable))
				{
					ReflectableAnalyzer anal = new ReflectableAnalyzer(compType, this.extLoader);
					if (anal.CanRepresentAsReflectable()) subValue = anal.PopulateTarget((DataTypeReflectable) initValue,TokenMapping);
				}
				Array.set(arr, i, subValue);
			}
		}
		return arr;
	}

	public DataTypeReflectable ProduceReflectable(Object Value) throws ExecutionValidationException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		if (!Value.getClass().getName().equals(this.Reflectable.getName())) return null;
		if (this.Reflectable.isArray()) return null;
		this.UnderInvestigation.clear();
		this.UnderInvestigation.add(this.Reflectable.getName());
		DataTypeReflectable dt = new DataTypeReflectable();
		HashSet<ReflectableAnalyzerMemberInfo> firstLevel = this.GetFirstLevelGettersSetters();
		List<ReflectableItem> items = new ArrayList<ReflectableItem>();
		for (ReflectableAnalyzerMemberInfo nfo : firstLevel)
		{
			IDataType.DataTypes nonArrayPrim = this.IsDirectPrimitive(nfo.type);
			if (nonArrayPrim != null)
			{
				ReflectableItem item = new ReflectableItem();
				item.Name = nfo.name;
				item.Token = "[execprfBeginToken]" + nfo.name + "[execprfEndToken]";
				if (!nfo.type.isArray()) item.Value = DataTypeUtils.GetDataType(nonArrayPrim);
				else
				{
					int dims = DataTypeUtils.CountDimentionsOfObjectArrayCode(nfo.type.getName());
					StringBuilder compdtsBuf=new StringBuilder();
					String compdts = "";
					for (int i = 0; i < dims; i += 1) compdtsBuf.append("[");
					compdtsBuf.append(nonArrayPrim.toString());
					compdts = compdtsBuf.toString();
					item.Value = DataTypeUtils.GetDataType(IDataType.DataTypes.Array);
					Class<?> arrayBaseComponentType = this.GetBasePrimitiveClassOfArray(nfo.type);
					if (arrayBaseComponentType == null) throw new ExecutionValidationException("Expecting primitive type but found " + nfo.type);
					((DataTypeArray) item.Value).SetDefaultComponentType(arrayBaseComponentType.getName());
					((DataTypeArray) item.Value).SetArrayClassCode(compdts);
				}
				item.Value.SetValue(nfo.getter.invoke(Value, (Object[]) null));
				items.add(item);
			}
			else
			{
				ReflectableItem item = new ReflectableItem();
				item.Name = nfo.name;
				item.Token = "[execprfBeginToken]" + nfo.name + "[execprfEndToken]";
				if (!nfo.type.isArray())
				{
					item.Value = new DataTypeReflectable();
					ReflectableAnalyzer subanal = new ReflectableAnalyzer(nfo.type, this.extLoader);
					if (!subanal.CanRepresentAsReflectable()) throw new ExecutionValidationException("Expecting elememnt to be reflectable but got " + nfo.type);
					item.Value.SetValue(subanal.ProduceReflectable(nfo.getter.invoke(Value, (Object[]) null)));
				}
				else
				{
					int dims = DataTypeUtils.CountDimentionsOfObjectArrayCode(nfo.type.getName());
					StringBuilder compdtsBuf = new StringBuilder();
					String compdts = "";
					for (int i = 0; i < dims; i += 1) compdtsBuf.append("[");
					compdtsBuf.append(IDataType.DataTypes.Reflectable.toString());
					compdts =compdtsBuf.toString();
					item.Value = DataTypeUtils.GetDataType(IDataType.DataTypes.Array);
					Class<?> arrayBaseComponentType = ReflectableAnalyzer.GetBaseComponentType(nfo.type, this.extLoader);
					if (arrayBaseComponentType == null) throw new ExecutionValidationException("Expecting non primitive type but found " + nfo.type);
					((DataTypeArray) item.Value).SetDefaultComponentType(arrayBaseComponentType.getName());
					((DataTypeArray) item.Value).SetArrayClassCode(compdts);
					item.Value.SetValue(nfo.getter.invoke(Value, (Object[]) null));
				}
				items.add(item);
			}
		}
		ReflectableItem[] itemsArray = items.toArray(new ReflectableItem[0]);
		dt.SetValue(itemsArray);
		return dt;
	}

	private HashSet<ReflectableAnalyzerMemberInfo> GetFirstLevelGettersSetters()
	{
		Hashtable<String, ReflectableAnalyzerMemberInfo> getters = new Hashtable<String, ReflectableAnalyzerMemberInfo>();
		Hashtable<String, ReflectableAnalyzerMemberInfo> setters = new Hashtable<String, ReflectableAnalyzerMemberInfo>();
		HashSet<ReflectableAnalyzerMemberInfo> paired = new HashSet<ReflectableAnalyzerMemberInfo>();

		Method[] methods = ReflectableAnalyzer.GetBaseComponentType(this.Reflectable, this.extLoader).getMethods();
		for (Method m : methods)
		{
			logger.debug("Checking method " + m.toString());
			if (!Modifier.isPublic(m.getModifiers())) continue;
			if (Modifier.isStatic(m.getModifiers())) continue;
			if (Modifier.isAbstract(m.getModifiers())) continue;
			if (m.getName().toUpperCase().startsWith("get".toUpperCase()))
			{
				String name = m.getName().substring("get".length());
				if (name.length() == 0) continue;
				if (m.getParameterTypes().length != 0) continue;
				if (m.getReturnType().getName().equals("void")) continue;
				getters.put(name, new ReflectableAnalyzerMemberInfo(name, m.getReturnType(), m, null));
			}
			if (m.getName().toUpperCase().startsWith("set".toUpperCase()))
			{
				String name = m.getName().substring("set".length());
				if (name.length() == 0) continue;
				if (m.getParameterTypes().length != 1) continue;
				if (!m.getReturnType().getName().equals("void")) continue;
				setters.put(name, new ReflectableAnalyzerMemberInfo(name, m.getParameterTypes()[0], null, m));
			}
		}
		if (getters.size() == 0 || setters.size() == 0) return paired;
		for (Map.Entry<String, ReflectableAnalyzerMemberInfo> entry : getters.entrySet())
		{
			if (!setters.containsKey(entry.getKey())) continue;
			if (!entry.getValue().type.getName().equals(setters.get(entry.getKey()).type.getName())) continue;
			paired.add(new ReflectableAnalyzerMemberInfo(entry.getKey(), entry.getValue().type, entry.getValue().getter, setters.get(entry.getKey()).setter));
		}
		if (paired.size() == 0) return paired;
		HashSet<ReflectableAnalyzerMemberInfo> toDel = new HashSet<ReflectableAnalyzerMemberInfo>();
		for (ReflectableAnalyzerMemberInfo entry : paired)
		{
			IDataType.DataTypes prim = this.IsPrimitive(ReflectableAnalyzer.GetBaseComponentType(entry.type, this.extLoader));
			if (prim != null) continue;
			else
			{
				Class<?> nonPrimCompType = ReflectableAnalyzer.GetBaseComponentType(entry.type, this.extLoader);
				if (this.UnderInvestigation.contains(nonPrimCompType.getName()))
				{
					toDel.add(entry);
					continue;
				}
				ReflectableAnalyzer suban = new ReflectableAnalyzer(nonPrimCompType, this.UnderInvestigation, this.extLoader);
				if (suban.CanRepresentAsReflectable())
				{
					this.UnderInvestigation.remove(nonPrimCompType.getName());
					continue;
				}
				else
				{
					this.UnderInvestigation.remove(nonPrimCompType.getName());
					toDel.add(entry);
				}
			}
		}
		for (ReflectableAnalyzerMemberInfo entry : toDel)
			paired.remove(entry);
		return paired;
	}

	private boolean GetterSettersExistForFields()
	{
		Hashtable<String, Class<?>> getters = new Hashtable<String, Class<?>>();
		Hashtable<String, Class<?>> setters = new Hashtable<String, Class<?>>();
		Hashtable<String, Class<?>> paired = new Hashtable<String, Class<?>>();
		Method[] methods = ReflectableAnalyzer.GetBaseComponentType(this.Reflectable, this.extLoader).getMethods();
		for (Method m : methods)
		{
			logger.debug("Checking method " + m.toString());
			if (!Modifier.isPublic(m.getModifiers()))
			{
				logger.debug("Method is not public");
				continue;
			}
			if (Modifier.isStatic(m.getModifiers()))
			{
				logger.debug("Method is static");
				continue;
			}
			if (Modifier.isAbstract(m.getModifiers()))
			{
				logger.debug("Method is abstract");
				continue;
			}
			if (m.getName().toUpperCase().startsWith("get".toUpperCase()))
			{
				logger.debug("method is getter");
				String name = m.getName().substring("get".length());
				if (name.length() == 0)
				{
					logger.debug("no name could be retrieved");
					continue;
				}
				if (m.getParameterTypes().length != 0)
				{
					logger.debug("not empty parameter list");
					continue;
				}
				if (m.getReturnType().getName().equals("void"))
				{
					logger.debug("void return value");
					continue;
				}
				getters.put(name, m.getReturnType());
			}
			if (m.getName().toUpperCase().startsWith("set".toUpperCase()))
			{
				logger.debug("method is setter");
				String name = m.getName().substring("set".length());
				if (name.length() == 0)
				{
					logger.debug("no name could be retrieved");
					continue;
				}
				if (m.getParameterTypes().length != 1)
				{
					logger.debug("not single parameter");
					continue;
				}
				if (!m.getReturnType().getName().equals("void"))
				{
					logger.debug("non void return value");
					continue;
				}
				setters.put(name, m.getParameterTypes()[0]);
			}
		}
		if (getters.size() == 0 || setters.size() == 0)
		{
			logger.debug("setters or getters are empty");
			return false;
		}
		for (Map.Entry<String, Class<?>> entry : getters.entrySet())
		{
			if (!setters.containsKey(entry.getKey()))
			{
				logger.debug("no respective setter for getter " + entry.getKey() + " found");
				continue;
			}
			if (!entry.getValue().getName().equals(setters.get(entry.getKey()).getName()))
			{
				logger.debug("mathcing getter (" + entry.getValue().getName() + ") and setter (" + setters.get(entry.getKey()).getName() + ") have incompatible types");
				continue;
			}
			logger.debug("mathcing getter (" + entry.getValue().getName() + ") and setter (" + setters.get(entry.getKey()).getName() + ") found");
			paired.put(entry.getKey(), entry.getValue());
		}
		if (paired.size() == 0)
		{
			logger.debug("No matched found");
			return false;
		}
		HashSet<String> toDel = new HashSet<String>();
		for (Map.Entry<String, Class<?>> entry : paired.entrySet())
		{
			IDataType.DataTypes prim = this.IsPrimitive(ReflectableAnalyzer.GetBaseComponentType(entry.getValue(), this.extLoader));
			if (prim != null)
			{
				logger.debug("getter seter for " + entry.getKey() + " has valid primitive component type " + prim.toString());
				continue;
			}
			else
			{
				Class<?> nonPrimCompType = ReflectableAnalyzer.GetBaseComponentType(entry.getValue(), this.extLoader);
				if (this.UnderInvestigation.contains(nonPrimCompType.getName()))
				{
					logger.error("Possible self referencing in decleration. Not a reflectable");
					toDel.add(entry.getKey());
					continue;
				}
				ReflectableAnalyzer suban = new ReflectableAnalyzer(nonPrimCompType, this.UnderInvestigation, this.extLoader);
				if (suban.CanRepresentAsReflectable())
				{
					logger.debug("getter seter for " + entry.getKey() + " has valid non primitive component type " + nonPrimCompType.getName());
					this.UnderInvestigation.remove(nonPrimCompType.getName());
					continue;
				}
				else
				{
					logger.debug("getter seter for " + entry.getKey() + " has non valid component type " + nonPrimCompType.getName());
					this.UnderInvestigation.remove(nonPrimCompType.getName());
					toDel.add(entry.getKey());
				}
			}
		}
		for (String entry : toDel)
			paired.remove(entry);
		if (paired.size() == 0)
		{
			logger.debug("No getter setter left");
			return false;
		}
		return true;
	}

	private Class<?> GetBasePrimitiveClassOfArray(Class<?> Array)
	{
		Class<?> ret = null;
		if (!Array.isArray()) ret = Array;
		int index = Array.getName().lastIndexOf("[");
		String com = Array.getName().substring(index + 1);
		if (com.startsWith("L")) com = com.substring(1);
		if (com.endsWith(";")) com = com.substring(0, com.length() - 1);
		logger.debug("after string manipulation will check type of " + com);
		if (com.equals("Z")) ret = boolean.class;
		else if (com.equals("I")) ret = int.class;
		else if (com.equals("F")) ret = float.class;
		else if (com.equals("D")) ret = double.class;
		else if (com.equals(String.class.getName())) ret = String.class;
		else if (com.equals(Boolean.class.getName())) ret = Boolean.class;
		else if (com.equals(Double.class.getName())) ret = Double.class;
		else if (com.equals(Float.class.getName())) ret = Float.class;
		else if (com.equals(Integer.class.getName())) ret = Integer.class;
		else if (com.equals(URI.class.getName())) ret = URI.class;
		logger.debug("Base primitive Component of " + Array.getName() + " is " + (ret == null ? "null" : ret.getName()));
		return ret;
	}

	// same as next?
	private IDataType.DataTypes IsDirectPrimitive(Class<?> componentType)
	{
		IDataType.DataTypes ret = null;
		if (componentType == null) ret = null;
		else if (componentType.getName().equals(int.class.getName())) ret = IDataType.DataTypes.IntegerPrimitive;
		else if (componentType.getName().equals(float.class.getName())) ret = IDataType.DataTypes.FloatPrimitive;
		else if (componentType.getName().equals(boolean.class.getName())) ret = IDataType.DataTypes.BooleanPrimitive;
		else if (componentType.getName().equals(double.class.getName())) ret = IDataType.DataTypes.DoublePrimitive;
		else if (componentType.getName().equals(Integer.class.getName())) ret = IDataType.DataTypes.IntegerClass;
		else if (componentType.getName().equals(Float.class.getName())) ret = IDataType.DataTypes.FloatClass;
		else if (componentType.getName().equals(Boolean.class.getName())) ret = IDataType.DataTypes.BooleanClass;
		else if (componentType.getName().equals(Double.class.getName())) ret = IDataType.DataTypes.DoubleClass;
		else if (componentType.getName().equals(String.class.getName())) ret = IDataType.DataTypes.String;
		else if (componentType.getName().equals(URI.class.getName())) ret = IDataType.DataTypes.ResultSet;
		else if (componentType.isArray()) ret = this.IsDirectPrimitive(componentType.getComponentType());
		return ret;
	}

	private IDataType.DataTypes IsPrimitive(Class<?> componentType)
	{
		IDataType.DataTypes ret = null;
		if (componentType == null) ret = null;
		else if (componentType.getName().equals(int.class.getName())) ret = IDataType.DataTypes.IntegerPrimitive;
		else if (componentType.getName().equals(float.class.getName())) ret = IDataType.DataTypes.FloatPrimitive;
		else if (componentType.getName().equals(boolean.class.getName())) ret = IDataType.DataTypes.BooleanPrimitive;
		else if (componentType.getName().equals(double.class.getName())) ret = IDataType.DataTypes.DoublePrimitive;
		else if (componentType.getName().equals(Integer.class.getName())) ret = IDataType.DataTypes.IntegerClass;
		else if (componentType.getName().equals(Float.class.getName())) ret = IDataType.DataTypes.FloatClass;
		else if (componentType.getName().equals(Boolean.class.getName())) ret = IDataType.DataTypes.BooleanClass;
		else if (componentType.getName().equals(Double.class.getName())) ret = IDataType.DataTypes.DoubleClass;
		else if (componentType.getName().equals(String.class.getName())) ret = IDataType.DataTypes.String;
		else if (componentType.getName().equals(URI.class.getName())) ret = IDataType.DataTypes.ResultSet;
		else if (componentType.isArray()) ret = this.IsPrimitive(ReflectableAnalyzer.GetBaseComponentType(componentType, this.extLoader));
		logger.debug("Base Component of " + componentType.getName() + " is " + ret);
		return ret;
	}

	public static Class<?> GetBaseComponentType(Class<?> Array, IExternalClassLoader extLoader)
	{
		Class<?> ret = null;
		if (!Array.isArray()) ret = Array;
		int index = Array.getName().lastIndexOf("[");
		String com = Array.getName().substring(index + 1);
		if (com.startsWith("L")) com = com.substring(1);
		if (com.endsWith(";")) com = com.substring(0, com.length() - 1);
		logger.debug("after string manipulation will check type of " + com);
		if (com.equals("Z")) ret = boolean.class;
		else if (com.equals("I")) ret = int.class;
		else if (com.equals("F")) ret = float.class;
		else if (com.equals("D")) ret = double.class;
		else if (com.equals(String.class.getName())) ret = String.class;
		else if (com.equals(Boolean.class.getName())) ret = Boolean.class;
		else if (com.equals(Double.class.getName())) ret = Double.class;
		else if (com.equals(Float.class.getName())) ret = Float.class;
		else if (com.equals(Integer.class.getName())) ret = Integer.class;
		else if (com.equals(URI.class.getName())) ret = URI.class;
		if (ret == null)
		{
			try
			{
				ret = Class.forName(com);
			} catch (Exception ex)
			{
				if (extLoader != null)
				{
					try
					{
						ret = extLoader.FindClass(com);
					} catch (Exception exx)
					{
						logger.debug("Error retrieving class " + com, exx);
						ret = null;
					}
				}
				else
				{
					logger.debug("Error retrieving class " + com, ex);
					ret = null;
				}
			}
		}
		logger.debug("Base Component of " + Array.getName() + " is " + (ret == null ? "null" : ret.getName()));
		return ret;
	}
}
