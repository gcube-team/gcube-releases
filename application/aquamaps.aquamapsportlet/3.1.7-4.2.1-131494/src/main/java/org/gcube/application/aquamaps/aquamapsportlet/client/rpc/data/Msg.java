package org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Msg implements IsSerializable {
boolean status;
String msg;

public Msg() {
}
public Msg(boolean status,String msg){
	this.status=status;
	this.msg=msg;
}
public boolean getStatus() {
	return status;
}
public void setStatus(boolean status) {
	this.status = status;
}
public String getMsg() {
	return msg;
}
public void setMsg(String msg) {
	this.msg = msg;
}
@Override
public String toString() {
	return "Msg [status=" + status + ", msg=" + msg + "]";
}
}
