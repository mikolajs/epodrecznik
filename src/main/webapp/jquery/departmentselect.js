 var DepartmentSelect = dejavu.Class.declare({
    	  selectDepart: '#departments',
    	  selectSubj: '#subjects',
    	  inputDepart: '#departmentHidden',
    	  
    	  initialize: function(){
    		  this.selectClassGroupForSubject(this);
    		  this._prepareDepartSelect();
    		  this._bindSubjectChange();
    		  this._bindDepartmentChange();
    	  },
    	  
    	  _prepareDepartSelect: function(){
    		  var depID = $(this.inputDepart).val();
    		  if(depID != "000000000000000000000000") {
    		  $(this.selectDepart).children('option[value="'+ depID +'"]').attr('selected',true);
    		  }
    		  else {
    			 this.selectFirstInActiveOptionClass(this);
    		  }
    	  },
    	  
    	  clearDepartmentSelect: function(){
    		  var options = document.getElementById("departments").childNodes;
    		  for(i in options) {
    			  if(options[i].localName == 'option') options[i].removeAttribute('selected');
    			  
    		  };
    	  }, 
    	  
    	  selectClassGroupForSubject: function(self){
    		  var subID = $(self.selectSubj).children("option:selected").val();
    		  $(self.selectDepart).children('option').hide();
    		  $(self.selectDepart).children('option.'+subID).show();  
    	  },
    	  
    	  selectFirstInActiveOptionClass: function(self){
    		  var subID = $(self.selectSubj).children("option:selected").val();  
    		  var options = document.getElementById("departments").childNodes;
    		  var depID = "";
    		  for(i in options) {
    			  if(options[i].localName == 'option' ) {
    				  var id = options[i].getAttribute('class');
    				  if (subID == id) {
    					  options[i].selected = true;
    					  depID =  options[i].getAttribute('value');
    					  break;
    				  }
    			  }
    		 }	 
    		  $(self.inputDepart).val(depID);
    	  },
    	  
    	  
    	  _bindSubjectChange: function(){
    		  var self = this;
    		  $(this.selectSubj).change(function(){
    			  self.clearDepartmentSelect();
    			  self.selectClassGroupForSubject(self);
    			  self.selectFirstInActiveOptionClass(self);
    		  });
    	  },
    	  
    	  _bindDepartmentChange: function(){
    		  var self = this;
    		  $(this.selectDepart).change(function(){
    			 var depID = $(this).children("option:selected").val();
    			 $(self.inputDepart).val(depID);
    		  });
    	  }    	  
      });