package gr.cite.regional.data.collection.dataaccess.daos;

import gr.cite.regional.data.collection.dataaccess.dsd.ColumnAndType;
import gr.cite.regional.data.collection.dataaccess.dsd.Field;
import gr.cite.regional.data.collection.dataaccess.entities.Annotation;
import gr.cite.regional.data.collection.dataaccess.entities.Cdt;
import gr.cite.regional.data.collection.dataaccess.entities.DataSubmission;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnitUtil;
import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public interface CdtDao/* extends Dao<Cdt, UUID>*/ {
	public void createTable(String tableName, List<ColumnAndType> dmColumnsAndTypes);
	public void deleteTable(String tableNameSuffix);
	public Cdt create(Cdt cdt, List<ColumnAndType> dmColumnsAndTypes, String tableNameSuffix);
	public Cdt update(Cdt cdt, List<ColumnAndType> dmColumnsAndTypes, String tableNameSuffix);
	public Cdt read(UUID id, String tableNameSuffix);
	public List<Cdt> getByDataSubmissionId(Integer dataSubmissionId, String tableNameSuffix, List<String> dmColumns);
}
