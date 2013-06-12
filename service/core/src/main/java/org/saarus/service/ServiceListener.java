package org.saarus.service;

public interface ServiceListener {
	public void onInit(Service service) ;
	public void onStart(Service service) ;
	public void onPause(Service service) ;
	public void onResume(Service service) ;
	public void onEvent(Service service, ServiceEvent event) ;
	public void onStop(Service service) ;
	public void onDestroy(Service service) ;
}
