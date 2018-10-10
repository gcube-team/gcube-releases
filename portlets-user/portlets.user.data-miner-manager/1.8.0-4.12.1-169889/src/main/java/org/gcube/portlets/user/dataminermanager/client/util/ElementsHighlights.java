package org.gcube.portlets.user.dataminermanager.client.util;

import com.allen_sauer.gwt.log.client.Log;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ElementsHighlights {

	public ElementsHighlights() {

	}

	public String createLinkFromText(String text) {
		Log.info("Message: " + text);
		if (text == null || text.isEmpty()) {
			return text;
		}

		boolean end = false;
		StringBuilder result = new StringBuilder();
		String mes = new String(text);

		while (!end) {
			if (mes != null && !mes.isEmpty()) {
				if (mes.contains("http") || mes.contains("https")) {
					int httpIndex = mes.indexOf("http");
					int httpsIndex = mes.indexOf("https");
					if (httpIndex == -1) {
						if (httpsIndex == -1) {
							result.append(mes);
							end = true;
						} else {
							String prefix = mes.substring(0, httpsIndex);
							result.append(prefix);

							String checkTerminator;
							if (httpsIndex > 0) {
								checkTerminator = mes.substring(httpsIndex - 1, httpsIndex);
								if (checkTerminator.compareTo(" ") != 0 && checkTerminator.compareTo("\"") != 0
										&& checkTerminator.compareTo("'") != 0) {
									Log.debug("Terminator Found:" + checkTerminator);
									checkTerminator = " ";
									Log.debug("Terminator Set:" + checkTerminator);
									
								} else {
									Log.debug("Terminator Set:" + checkTerminator);
								}
							} else {
								checkTerminator = " ";
								Log.debug("Terminator Set:" + checkTerminator);
							}


							mes = mes.substring(httpsIndex, mes.length());

							int terminator = mes.indexOf(checkTerminator);
							if (terminator == -1) {
								if(mes.contains("</")){
									int j=mes.indexOf("</");
									String extractedUrl=mes.substring(0, j);
									result.append("<a href='" + extractedUrl + "' target='_blank'>" + extractedUrl + "</a>");
									result.append(mes.substring(j));
								} else {
									result.append("<a href='" + mes + "' target='_blank'>" + mes + "</a>");
								}
								end = true;
							} else {
								String extractedUrl;
								if (checkTerminator == " ") {
									extractedUrl = mes.substring(0, terminator+1);
								} else {
									extractedUrl = mes.substring(0, terminator);
								}
								

								if(extractedUrl.contains("</")){
									int k=extractedUrl.indexOf("</");
									extractedUrl=extractedUrl.substring(0, k);
								}
								
								Log.debug("extractedUrl: "+extractedUrl);
								result.append(
										"<a href='" + extractedUrl + "' target='_blank'>" + extractedUrl + "</a>");
								mes = mes.substring(terminator, mes.length());
							}
						}

					} else {
						if (httpsIndex == -1) {
							String prefix = mes.substring(0, httpIndex);
							result.append(prefix);

							String checkTerminator;
							if (httpIndex > 0) {
								checkTerminator = mes.substring(httpIndex - 1, httpIndex);
								if (checkTerminator.compareTo(" ") != 0 && checkTerminator.compareTo("\"") != 0
										&& checkTerminator.compareTo("'") != 0) {
									Log.debug("Terminator Found:" + checkTerminator);
									checkTerminator = " ";
									Log.debug("Terminator Set:" + checkTerminator);
									
								} else {
									Log.debug("Terminator Set:" + checkTerminator);
								}
							} else {
								checkTerminator = " ";
								Log.debug("Terminator Set:" + checkTerminator);
							}

							mes = mes.substring(httpIndex, mes.length());
							int terminator = mes.indexOf(checkTerminator);
							Log.debug("TerminatorCheck found: "+terminator);
							if (terminator == -1) {
								if(mes.contains("</")){
									int j=mes.indexOf("</");
									String extractedUrl=mes.substring(0, j);
									result.append("<a href='" + extractedUrl + "' target='_blank'>" + extractedUrl + "</a>");
									result.append(mes.substring(j));
								} else {
									result.append("<a href='" + mes + "' target='_blank'>" + mes + "</a>");
								}
								end = true;
							} else {
								String extractedUrl;
								if (checkTerminator == " ") {
									extractedUrl = mes.substring(0, terminator+1);
								} else {
									extractedUrl = mes.substring(0, terminator);
								}
								

								if(extractedUrl.contains("</")){
									int k=extractedUrl.indexOf("</");
									extractedUrl=extractedUrl.substring(0, k);
								}
								
								Log.debug("extractedUrl: "+extractedUrl);
								result.append(
										"<a href='" + extractedUrl + "' target='_blank'>" + extractedUrl + "</a>");
								mes = mes.substring(terminator, mes.length());
							}
						} else {
							if (httpsIndex <= httpIndex) {
								String prefix = mes.substring(0, httpsIndex);
								result.append(prefix);

								String checkTerminator;
								if (httpsIndex > 0) {
									checkTerminator = mes.substring(httpsIndex - 1, httpsIndex);
									if (checkTerminator.compareTo(" ") != 0 && checkTerminator.compareTo("\"") != 0
											&& checkTerminator.compareTo("'") != 0) {
										Log.debug("Terminator Found:" + checkTerminator);
										checkTerminator = " ";
										Log.debug("Terminator Set:" + checkTerminator);
										
									} else {
										Log.debug("Terminator Set:" + checkTerminator);
									}
								} else {
									checkTerminator = " ";
									Log.debug("Terminator Set:" + checkTerminator);
								}


								mes = mes.substring(httpsIndex, mes.length());
								int terminator = mes.indexOf(checkTerminator);

								if (terminator == -1) {
									if(mes.contains("</")){
										int j=mes.indexOf("</");
										String extractedUrl=mes.substring(0, j);
										result.append("<a href='" + extractedUrl + "' target='_blank'>" + extractedUrl + "</a>");
										result.append(mes.substring(j));
									} else {
										result.append("<a href='" + mes + "' target='_blank'>" + mes + "</a>");
									}
									end = true;
								} else {
									String extractedUrl;
									if (checkTerminator == " ") {
										extractedUrl = mes.substring(0, terminator);
									} else {
										extractedUrl = mes.substring(0, terminator - 1);
									}
									

									if(extractedUrl.contains("</")){
										int k=extractedUrl.indexOf("</");
										extractedUrl=extractedUrl.substring(0, k);
									}
									
									Log.debug("extractedUrl: "+extractedUrl);
									result.append(
											"<a href='" + extractedUrl + "' target='_blank'>" + extractedUrl + "</a>");
									mes = mes.substring(terminator, mes.length());
								}
							} else {
								String prefix = mes.substring(0, httpIndex);
								result.append(prefix);

								String checkTerminator;
								if (httpIndex > 0) {
									checkTerminator = mes.substring(httpIndex - 1, httpIndex);
									if (checkTerminator.compareTo(" ") != 0 && checkTerminator.compareTo("\"") != 0
											&& checkTerminator.compareTo("'") != 0) {
										Log.debug("Terminator Found:" + checkTerminator);
										checkTerminator = " ";
										Log.debug("Terminator Set:" + checkTerminator);
										
									} else {
										Log.debug("Terminator Set:" + checkTerminator);
									}
								} else {
									checkTerminator = " ";
									Log.debug("Terminator Set:" + checkTerminator);
								}


								mes = mes.substring(httpIndex, mes.length());
								int terminator = mes.indexOf(checkTerminator);

								if (terminator == -1) {
									if(mes.contains("</")){
										int j=mes.indexOf("</");
										String extractedUrl=mes.substring(0, j);
										result.append("<a href='" + extractedUrl + "' target='_blank'>" + extractedUrl + "</a>");
										result.append(mes.substring(j));
									} else {
										result.append("<a href='" + mes + "' target='_blank'>" + mes + "</a>");
									}
									end = true;
								} else {
									String extractedUrl;
									if (checkTerminator == " ") {
										extractedUrl = mes.substring(0, terminator);
									} else {
										extractedUrl = mes.substring(0, terminator - 1);
									}
									
									if(extractedUrl.contains("</")){
										int k=extractedUrl.indexOf("</");
										extractedUrl=extractedUrl.substring(0, k);
									}
									
									Log.debug("extractedUrl: "+extractedUrl);
									result.append(
											"<a href='" + extractedUrl + "' target='_blank'>" + extractedUrl + "</a>");
									mes = mes.substring(terminator, mes.length());

								}
							}
						}

					}

				} else {
					result.append(mes);
					end = true;
				}
			} else {
				end = true;
			}
		}
		Log.info("New Message: " + result.toString());
		return result.toString();
	}
}
