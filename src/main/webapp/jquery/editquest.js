
var EditQuest =  dejavu.Class.declare({
	isOpen : false,
	oTable : $(),
	
	initialize : function() {
		var self = this;
		$('#questList tbody tr').each(function(){
			$(this).click(function(){
				self.editQuestion(this);
			});
		});
	},
	
	deleteQuestion : function(id) {
		alert('DELETE ' + id);
		this._deleteRow(id);
		$('#questEditor').dialog('close');
	}, 
	
	insertQuestion : function(id) {
		alert('INSERT ' + id);
		var array = new Array();
		if(id == 0) {
			//howto check new or edit?????
			this._insertNewRow(array, id)
		}
		else {
			this._editRow(array, id);
		}
		$('#questEditor').dialog('close');
	},
	
	prepareToSave : function() {
		this._insertDataFromCKEditorToTextarea();
		var fakeStr = this._getFakeAnswerStringFromInputs();		
		$('#wrongQuest').val(fakeStr);
		$('#saveQuest').trigger('click');
		return false;
	},
	
    addFakeAnswer : function() {
    	var fake = $('#fakeAdd').val();
    	fake = jQuery.trim(fake);
    	if(fake.length > 0) {
    		$('#fakeAnswerList').append('<li>'+ fake + 
    				'<img src="/images/delico_min.png" onclick="editQuest.delFakeAnswer(this)"/></li>');
    		$('#fakeAdd').val("");
    	}
    	
    },
    
    delFakeAnswer : function(elem) {
    	$(elem).parent('li').remove();
    },
    
    startNewQuest : function() {
    	this._resetFormEdit();
    	if(this.isOpen) return;
    	this.isOpen = true;
    	$('#questEditor').dialog('open');
    },
    
    editQuestion : function(elem) {
    	this._resetFormEdit();
    	var $tr = $(elem);
    	var id = $tr.attr('id');
    	$('#idQuest').val(id);
    	$tr.children('td').each(function(index){
    		var $td = $(this);
    		switch (index) {
    		case 0:
    			CKEDITOR.instances.questionQuest.setData($td.text());
    			break;
    		case 1:
    			$('#answerQuest').val($td.text());
    			break;
    		case 2:
    			var array = new Array();
    			$ul = $('#fakeAnswerList');
    			$td.children('span.wrong').each(function(){
    				array.push($(this).text());
    			});
    			for(i in array){
    				$ul.append('<li>'+ array[i] +
    						'<img src="/images/delico_min.png" onclick="editQuest.delFakeAnswer(this)"/></li>');
    			}
    			break;
    		case 3:
    			$('#dificultQuest option[value="'+$td.text()+'"]').attr("selected","selected");
    			//var ind = parseInt($td.text()) - 2;
    			//document.getElementById('dificultQuest').selectedIndex = ind;
    			break;
    		case 4:
    			if($td.text() == 'TAK') document.getElementById('publicQuest').checked = true;
    			else document.getElementById('publicQuest').checked = false;
    			break;
    		default:
    			break;
    		}
    	});
    	isOpen = true;
    	$('#questEditor').dialog('open');
    },
    
    _insertDataFromCKEditorToTextarea : function() {
    	$('#questionQuest').val(CKEDITOR.instances.questionQuest.getData());
    },
    
    _getFakeAnswerStringFromInputs : function() {
    	var fakeList = [];
		var fakes = $('#fakeAnswerList').children('li').each(function(){
			fakeList.push($(this).text());
		});
		
		var inInput = $('#fakeAdd').val();
		inInput = jQuery.trim(inInput);
		if(inInput.length > 0) fakeList.push(inInput);
		
		var fakeStr = fakeList.join(';');
		alert("Błędne odpowiedzi: " +fakeStr);
		return fakeStr;
    },
    
    _resetFormEdit : function() {
    	$('#questEditor').children('form').get(0).reset();
    	$('#fakeAnswerList').empty();
    },
    
    _getTrNodeContainsId : function (id) {
		var trNodes = self.oTable.fnGetNodes();
		for (i in trNodes) {	
			if (trNodes[i].id == id)
				return trNodes[i];
		}
	},
 
    _deleteRow = function(id) {
		var tr = this.getTrNodeContainsId(id);
		this.oTable.fnDeleteRow(tr);
	},
 
	_insertNewRow = function(array, id){
		var indexes = this.oTable.fnAddData(array);
		//add data tr id???
		return this.oTable.fnGetNodes(indexes[0]);
	},
 
 
	_editRow : function(array, id) {  
		var tr = self.getTrNodeContainsId(id);
		if (tr) self.oTable.fnUpdate(array, tr);  
	}
    
	
});