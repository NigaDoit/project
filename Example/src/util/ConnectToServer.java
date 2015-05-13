package util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class ConnectToServer {
	
	
	private URL text;
	private String domain;

	public ConnectToServer()	{
	}
	
	public URL getUrl()	{
		return text;
	}
	
	public void setDomain(String _domain)	{
		domain = _domain;
		init();
	}
	public void init()	{
		try {
			text = new URL(domain);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

