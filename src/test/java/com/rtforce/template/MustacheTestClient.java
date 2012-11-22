package com.rtforce.template;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.framework.TestClientBase;

public class MustacheTestClient extends TestClientBase {

  private static String MESSAGE_REPLY = "ok";
  
  private EventBus eb;

  @Override
  public void start() {
    super.start();
    eb = vertx.eventBus();
    JsonObject config = new JsonObject();
    config.putString("address", "rtforce.template");
    
    System.out.println(System.getProperty("vertx.name"));
    
    container.deployModule( System.getProperty("vertx.name") , config, 1, new Handler<String>() {
      public void handle(String res) {
        tu.appReady();
      }
    });
  }

  @Override
  public void stop() {
    super.stop();
  }

  public void testMustacheParsing() throws Exception {
	JsonObject jsonObj = new JsonObject();
	jsonObj.putString("name", "something/here.html");
    eb.send("rtforce.template", jsonObj, new Handler<Message<JsonObject>>() {
      public void handle(Message<JsonObject> reply) {
    	  System.out.println("has output parsing ... " + reply.body.getString("output"));
    	  tu.testComplete();
      }
    });
  }

}

