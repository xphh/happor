package cn.lechange.happor.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UriParser {

	private String uri;
	private String absoluteUri;
	private List<String> sections = new ArrayList<String>();
	private Map<String, String> params = new HashMap<String, String>();
	
	public UriParser(String uri) {
		this.uri = uri;
		
		int pos = uri.indexOf("?");
		if (pos >= 0) {
			absoluteUri = uri.substring(0, pos);
			String pStr = uri.substring(pos + 1);
			String pairs[] = pStr.split("&");
			for (String pair : pairs) {
				String nv[] = pair.split("=");
				if (nv.length == 2) {
					params.put(nv[0], nv[1]);
				}
			}
		} else {
			absoluteUri = uri;
		}
		sections.add(absoluteUri);
	}
	
	public String getAbsoluteUri() {
		return absoluteUri;
	}
	
	public boolean matches(String uriPattern) {
		Pattern pattern = Pattern.compile(uriPattern);
		Matcher matcher = pattern.matcher(uri);
		if (matcher.find()) {
			for (int i = 1; i <= matcher.groupCount(); i++) {
				sections.add(matcher.group(i));
			}
			return true;
		}
		return false;
	}
	
	public String getSection(int index) {
		return sections.get(index);
	}
	
	public String getParam(String name) {
		return params.get(name);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		UriParser up = new UriParser("/test/aaa?x=1&yy=abc");
		System.out.println(up.getAbsoluteUri());
		System.out.println(up.getParam("x"));
		System.out.println(up.getParam("yy"));
		if (up.matches("^/test/(\\w+)")) {
			System.out.println(up.getSection(0));
		}
	}

}
