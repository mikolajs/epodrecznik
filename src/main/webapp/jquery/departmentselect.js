 var DepartmentSelect = dejavu.Class.declare({
    	  selectDepart: '#departments',
    	  selectSubj: '#subjects',
    	  inputDepart: '#departmentHidden',
    	  
    	  initialize: function(){
    		  alert('Start departmentselect');
    		  this.selectClassGroupForSubject(this);
    		  this._prepareDepartSelect();
    		  this._bindSubjectChange();
    		  this._bindDepartmentChange();
    	  },
    	  
    	  _prepareDepartSelect: function(){
    		  var depID = $(this.inputDepart).val();
    		  if(depID != "000000000000000000000000") {
    		  $(this.selectDeprt).children('option[value='+ depID +']').attr('selected','selected');
    		  }
    		  else {
    			 this.selectFirstInActiveOptionClass(this);
    		  }
    	  },
    	  
    	  clearDepartmentSelect: function(self){
    		  $(self.selectDepart).find('option[selected="selected"').attr("selected","");
    	  },
    	  
    	  selectClassGroupForSubject: function(self){
    		  var subID = $(self.selectSubj).children("option[selected='selected']").val();
    		  $(self.selectDepart).find('option').hide();
    		  $(self.selectDepart).find('option.'+subID).show();  
    	  },
    	  
    	  selectFirstInActiveOptionClass: function(self){
    		  var subID = $(self.selectSubj).children("option[selected='selected']").val();   		  
    		  var depID = $(self.selectDepart).children('option.'+subID).first().attr('selected','selected').val();   		
    		  $(self.inputDepart).val(depID);
    	  },
    	  
    	  _bindSubjectChange: function(){
    		  var self = this;
    		  $(this.selectSubj).change(function(){
    			  self.selectClassGroupForSubject(self);
    			  self.clearDepartmentSelect(self);
    			  self.selectFirstInActiveOptionClass(self);
    		  });
    	  },
    	  
    	  _bindDepartmentChange: function(){
    		  var self = this;
    		  $(this.selectDepart).change(function(){
    			 var depID = $(this).children("option[selected='selected']").val();
    			 $(self.inputDepart).val(depID);
    		  });
    	  }
    	  
      });