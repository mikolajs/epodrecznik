var LessonEditor = dejavu.Class.declare({
       		$name: "LessonEditor", 
       		$extra: null,
       		sendFormId: "#send",
       		$listBody: null,
       		$json: null,
       		$window: null,
       		$edited: null,
       		table:null,
       		
       		
       		initialize : function(oTable) {
       			this.table = oTable;
       			//alert("prepare");
    			this._prepareVar();
    			this._bindBeforeSubmit();
    			//alert("List");
    			//alert("refresh");
    			this.refreshDataTable("");
    			
       			this._createList();
       			//alert("bindRefesh");
       			this._bindRefreshData();
				//alert("bindSortable");
				this._bindSortableList();
    		
		},
       		_prepareVar: function(){
       			this.$listBody = $('#lessonList').first();
       			$('#extraTextEdit').html($('#extraText').val());
       			var jsonStr = $('#json').val();
       			this.$json = eval('(' + jsonStr + ')');
       		},
       		
       		_bindBeforeSubmit: function(){
       			var self = this;
       			$(this.sendFormId).submit(function(){
       				alert("before submit");
       				self.copyFromEditorToInput();
       				alert($('#extraText').val());
       				var jsonString = "[";
       				$('#lessonList').children('li').each(function(){
       					var $this = $(this);
       					$title = $this.children('.title').first();
       					var title = $title.text();
           				var link =  $title.attr('name');
           				var depart =  $this.children('.depart').first().text();
           				var what =  $this.children('.what').children( 'img').first().attr('title');
           				jsonString += '{"what": "' + what +  '", "link": "' + link + '", "title":"' 
           				+ title +'", "depart":"' + depart + '"},';
       				});
       				jsonString += "]";
       				$('#json').val(jsonString);
       				$('#extraText').val($('#extraTextEdit').html().toString());
       				alert($('#extraText').val() + " J: " + jsonString );
       				return self.validate();
       			});
       		},
       		
       		copyFromEditorToInput: function(){
       			$('#extraText').val($('#extraTextEdit').html());     			
       		},
       		
       		_createList: function(){
       			var strItem = ""
       			for(i in this.$json){
       				strItem = this._createItem(this.$json[i]);
       				this.$listBody.append(strItem);
       			}
       		},
       		
       		_createItem:function(item){
       			var mapIco = {quest:"quiz.png", word:"document.png"};
   				var title = '<span class="title" name="' + item.link +'">' + item.title + '</span>';
   				var depart =  '<span class="depart">' + item.depart + '</span>';
   				var what =  '<span class="what"><img src="/images/' + mapIco[item.what] + '" name="' +item.what + '" title="'  + item.what + '" /></span>';
   				var del =  '<img class="imgDel" src="/images/delico_min.png" onclick="lessonEditor.deleteData(this);"/>';
   				return '<li>'+what+depart + del  + '<br/>'+ title +'</li>';
       		},
       		
       		_bindRefreshData:function(){
       			$('#getItemType').change(function(){
       				var $select = $(this);
       				//alert("change select");
       				var t = $select.children('option:selected').val();
       				var ajaxT = $('#hiddenAjaxText');
       				ajaxT.val(t);
       				ajaxT.get(0).onblur();
       			});
       		},
       		
       		refreshDataTable: function(data){
       			//alert("begin refresh " + this.$name);
       			var str = "";
       			if(data =="") str = $("#forDataTable").val();
       			else str = data
       			this.table.fnClearTable();
       			var json = eval( '('+str+ ')');
          	   this.table.fnAddData(json); 
          	  this.table.fnDraw();
          	  this._bindInsertData();
       		},
       		
       		createNewItem: function(tr, self){
       			//alert("createNewItem");
       			var item = new Object();
       			var aData = self.table.fnGetData(tr);
       			item.title = aData[1] ;
   				item.link= "id_" + aData[0];
   				item.depart = aData[2];
   				item.what = $('#getItemType option:selected').val();
   				var str = self._createItem(item);
   				self.$listBody.append(str);
       		},
       		
       		_bindInsertData: function(){
       			var self = this;
       			this.table.$('tr').each( function(){
       				$(this).click(function(){self.createNewItem(this, self);});
       			});
       		},
       	
       		deleteData: function(elem){
       			$(elem).parent('li').remove();
       		},
       		
       		_bindSortableList: function(){ 			
       			this.$listBody.sortable();
       		},
       		
       		
       });