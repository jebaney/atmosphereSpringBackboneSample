$(function () {
        "use strict";

        var content = $('#content');
        var input = $('#input');
        var modifyButton = $('#modifyButton');
        var status = $('#status');
        
        var entityManager = new EntityManager();

        input.keydown(function(e) {
            if (e.keyCode === 13) {
                var msg = $(this).val();
                
                console.log("keydown ", msg);
                
                addSubscription(msg);
                $(this).val("");
            }
        });
        
        modifyButton.click(function() {
        	console.log("modifyButton clicked");
        	modify();
        });

        function addMessage(id, firstName, lastName) {
            content.append('<p>' + id + ' -- ' + firstName + ' -- '  + lastName + '</p>');
        }
        
        
        function addSubscription(id)
        {
        	console.log("starting to add a new subscription " + id);
        	
        	var user = entityManager.fetch(id);
        	var view = new UserView({model : user});
    		$('#user-list').append(view.render().el);
        }
        
        
        function modify()
        {
        	var payload = '{"availability":"hello"}';
        	        	
        	entityManager.modify("a", document.location.toString() + 'svc/user/a/modify', payload);
        }
        
        
        function addMessage(message)
        {        	
        	var lists = $('#messages');
        	
        	var list = $('#' + message.id);
        	if (!(list.length)) {
        		lists.append("<hr/>");
        		list = $('<ul>', {id: '' + message.id + ''}).appendTo(lists);
        		console.log("creating list " + message.id);
        		//list.html = "<ul id=\"" + message.id + "\"></ul>";
        		//lists.append(list);
        	}
        	else {
        		console.log("list already exists or isn't empty " + message.id);
        	}
        	list.append("<li>" + message.id + " -- " + message.lastName + "</li>");
        	
        }
        
        var UserView = Backbone.View.extend({
        	tagName : 'li',
        	template: _.template($('#user-template').html()),
        	
        	initialize : function() {
        		this.listenTo(this.model, 'change', this.render);
        	},
        	
        	render : function() {
        		this.$el.html(this.template(this.model.toJSON()));
        		this.$el.highlight();
        		return this;
        	}
        });
        
        jQuery.fn.highlight = function() {
        	   $(this).each(function() {
        	        var el = $(this);
        	        el.before("<div/>");
        	        el.prev()
        	            .width(el.width())
        	            .height(el.height())
        	            .css({
        	                "position": "absolute",
        	                "background-color": "#ffff99",
        	                "opacity": ".9"   
        	            })
        	            .fadeOut(1000);
        	    });
        	};
        	
        	
                
        
    });