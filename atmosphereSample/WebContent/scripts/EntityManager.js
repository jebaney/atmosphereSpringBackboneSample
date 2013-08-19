/*
 * Represents the single communication channel between a client and the
 * server.  All client requests funnel through this class
 */

function EntityManager() {
	
	"use strict";
	
	var content = $('#content');
    var input = $('#input');
    var status = $('#status');
	
	var socket = $.atmosphere;
    var request = { url: document.location.toString() + 'svc/user/abc',
                    contentType : "application/json",
                    logLevel : 'debug',
                    transport : 'websocket' ,
                    trackMessageLength : false,
                    webSocketPathDelimiter : "@@",
                    fallbackTransport: 'long-polling'};


    request.onOpen = function(response) {
        content.html($('<p>', { text: 'Atmosphere connected using ' + response.transport }));
        console.log("onOpen: headers: " + response.headers);
        input.removeAttr('disabled').focus();
        status.text('Choose name:');
    };

    request.onMessage = function (response) {
        var message = response.responseBody;
        console.log("message on original channel " + message);
        try {
            var json = jQuery.parseJSON(message);
            var id = json.id;
            
            var entity = entityMap[id];
            if (!(entity)) {
            	// We got a message for an entity that we don't care about
            	console.log("Message received for an entity we don't want " + id);
            }
            else {
            	if (json.firstName !== entity.firstName) {
            		entity.set("firstName", json.firstName);
            	}
            	if (json.lastName !== entity.lastName) {
            		entity.set("lastName", json.lastName);
            	}
            	if (json.availability !== entity.availability) {
            		entity.set("availability", json.availability);
            	}
            }
            
        } catch (e) {
            console.log('Error handling broadcast ' + message + ' ---> ' + e.message);
            return;
        }
    };

    request.onClose = function(response) {
        
    };

    request.onError = function(response) {
        content.html($('<p>', { text: 'Sorry, but there\'s some problem with your '
            + 'socket or the server is down' }));
    };
    

    var subSocket = socket.subscribe(request);
    
    var topicToCallbackMap = new Object();
    
    var entityMap = new Object();   
    
    function globalCallback(response) {
    	if (response.state != "messageReceived") {
    		return;
    	}
    	console.log("global callback");
    }
    
    function subscribe(uuid, url, callback)
    {    	
    	console.log("adding subscription " + uuid);
    	if (callback) {
    		var callbacks = topicToCallbackMap[uuid];
        	if (!callbacks)
        	{
        		callbacks = new Array();
        	}
        	callbacks.push(callback);
        	topicToCallbackMap[uuid] = callbacks;
    	}    	
    	var fetchCommand = new Command();
    	fetchCommand.set({"id":uuid});
    	//subSocket.push(JSON.stringify(fetchCommand.toJSON()));
    	var location = document.location.toString() + 'svc/user/' + uuid;
    	var call = function(response) {
    		console.log("response " + response.responseBody);
    		if (response.state != "messageReceived") {
    			return;
    		}
    	};
    	
    	
    	var rq = socket.subscribe(location, globalCallback, $.atmosphere.request = {
    			logLevel : 'debug',
    			transport : 'websocket',
    			callback : call
    		});
    	return rq;
    };
    
    
    this.fetch = function (uuid)
    {
    	var entity = entityMap[uuid];    	
    	if (!(entity)) {
    		entity = new User();
        	entity.set("id", uuid);
    		// first time listening, subscribe
    		entityMap[uuid] = entity;
    		subscribe(uuid, null, null);
    	}
    	return entity;
    };
    
    this.modify = function(id, url, payload)
    {
    	var req = { url : url, data : payload };
        socket.publish(req);
    	/*
    	var command = new Command();
    	command.set({
    		"id" : id,
    		"url" : url + "/" + id,
    		"action" : "modify",
    		"payload" : payload
    	});
    	var stringy = JSON.stringify(command.toJSON());
    	console.log("calling publish..." + stringy);
    	subSocket.push(stringy);
    	*/
    };
    
    
    var Command = Backbone.Model.extend({
    	defaults : function(){
    		return {
    			"url" : "/",
    			"id" : "",
    			"action" : "",
    			"payload" : ""
    		};
    	}
    });
    
    var User = Backbone.Model.extend({
    	// default values
    	defaults : function(){
    		return {
    			"firstName" : "",
    			"lastName" : "fetching data...",
    			"availability" : ""
    		};
    	}   	
    });
	
};