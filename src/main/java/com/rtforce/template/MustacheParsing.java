package com.rtforce.template;

import java.io.Console;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.file.impl.PathAdjuster;
import org.vertx.java.core.json.JsonObject;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;

/**
 * Parsing Mustache based templates on request in the event-bus and send parsed documents back
 * 
 * @author Joris Hermans
 */
public class MustacheParsing extends BusModBase {

	private static final String DEFAULT_KEY = "*";
	private String address;
	private String templateDir;
	
	private Map<String, Object> paths;

	public void start() {
		super.start();
		
		address = getOptionalStringConfig("address", "rtforce.template");
		templateDir = getOptionalStringConfig("templateDir", "templates");
		
		JsonObject name_toPath = getOptionalObjectConfig("nameToPath", new JsonObject());
		paths = reparse(name_toPath.toMap());
		
		eb.registerHandler(address, new Handler<Message<JsonObject>>() {

			@Override
			public void handle(Message<JsonObject> event) {
				MustacheFactory mf = new DefaultMustacheFactory();
			    com.github.mustachejava.Mustache mustache = null;
			    StringWriter sw = new StringWriter();
			    String output = "", filePath = "";
				
			    try {
			    	System.out.println("before creating filePath " + event.body.getString("name"));
			    	filePath = templateDir + "\\" + getPath(event.body.getString("name"));
					mustache = mf.compile(new FileReader(filePath), filePath);
				    try {
				    	Map<String, Object> scope = event.body.toMap();
				    	
						mustache.execute(sw, scope).flush();
						output = sw.toString();
					} catch (IOException e) {
						output = "Something went wrong with the parsing of the document " + filePath;
					}
				} catch (FileNotFoundException e1) {
					output = "<p>File not found : " + filePath + " <br /> " + event.body.getString("name") + "</p>";
				}
				
				
			    JsonObject replyObject = new JsonObject();
			    replyObject.putString("output", output);
			    
			    event.reply(replyObject);
			}
		});
	}
	
	private Map<String, Object> reparse(Map<String, Object> map) {
		Map<String, Object> transformedObject = new HashMap<String, Object>();
		for (String key : map.keySet()) {
			Object obj = map.get(key);
			String[] moreKeys = key.split(";");
			for (String moreKey : moreKeys) {
				transformedObject.put(moreKey, obj);
			}
		}
		return transformedObject;
	}

	private String getPath(String name) {
		System.out.println("getPath " + name);
		if (paths.containsKey(name)) {
			return paths.get(name).toString();
		} else if (paths.containsKey(DEFAULT_KEY)) {
			return paths.get(DEFAULT_KEY).toString();
		}
		return name;
	}

	public void stop() {
		try {
			super.stop();
		} catch (Exception e) {
		}
	}
}
