var LessonEditor = dejavu.Class.declare({
       		$name: "LessonEditor", 
       		$editForm: null,
       		$sendForm: null,
       		$listBody: null,
       		$json: null,
       		$window: null,
       		$edited: null,
       		
       		initialize: function(){
       			this._prepareVar();
       			this._createList();
       			this._bindInsertData();
       			this._bindSortableList();
       			this._bindAddNewData();
       			
       		},
       		_createList: function(){
       			for(i in this.$json.items){
       				var item = this.$json.items[i];
       				var title = '<span class="title">' + item.title + '</span>';
       				var link =  '<span class="link">' + item.link + '</span>';
       				var desc =  '<br/><span class="desc">' + item.desc + '</span>';
       				var type =  '<span class="type">' + item.type + '</span>';
       				var del =  '<img src="/images/delico_min.png" onclick="lessonEditor.deleteData(this);"/>';
       				this.$listBody.append('<li>'+type+title+link+del+desc+'</li>');
       				this.bindDbClickEdit();
       			}
       		},
       		_prepareVar: function(){
       			this.$editForm = $('#edit').children('form').first();
       			this.$sendForm = $('#send').first();
       			this.$listBody = $('#lessonList').first();
       			var jsonStr = $('#json').val();
       			this.$json = eval('(' + "{'items': [] }" + ')');
				this.$window = $('#edit').dialog({
					modal:true,
					autoOpen:false,
					width:450,
					height: 300
				});
       			
       		},
       		
       		bindDbClickEdit: function(){
       			var self = this;
				this.$listBody.children('li').dblclick(function(){
					alert("DB CLICKED");
   					var $this =  $(this);
   					self.$edited = $this;
   					var title = $this.children('.title').first().text();
       				var link =  $this.children('.link').first().text();
       				var desc =  $this.children('.desc').first().text();
       				var type =  $this.children('.type').first().text();
       				self.$editForm.find('#isOld').val('y');
       				self.$editForm.find('#Theme').val(title);
       				self.$editForm.find('#Link').val(link);
       				self.$editForm.find('#Desc').val(desc);
       				self.$editForm.find('#Type option:selected').removeAttr('selected');
       				self.$editForm.find('#Type option').each(function(){
       					var $thisInner = $(this);
       					if($thisInner.val() == type) $thisInner.attr('selected','selected');
       				});
       				self.$window.dialog('open');
   				});
       		},
       		
       		createNewItem: function(self){
       			var titleIt = self.$editForm.find('#Theme').val();
   				var linkIt = self.$editForm.find('#Link').val();
   				var descIt = self.$editForm.find('#Info').val();
   				var typeIt = self.$editForm.find('#Type option:selected').val();
       			var title = '<span class="title">' + titleIt + '</span>';
   				var link =  '<span class="link">' + linkIt + '</span>';
   				var desc =  '<br/><span class="desc">' + descIt + '</span>';
   				var type =  '<span class="type">' + typeIt + '</span>';
   				var del =  '<img src="/images/delico_min.png" onclick="lessonEditor.deleteData(this);"/>';
   				self.$listBody.append('<li>'+type+title+link+del+desc+'</li>');
   				self.$listBody.children('li').each(function(){
   					$(this).unbind('dblclick');
   				});
   				self.bindDbClickEdit();
   				self.$window.dialog('close');
       		},
       		
       		changeOldData: function(self){
       			var titleIt = self.$editForm.find('#Theme').val();
   				var linkIt = self.$editForm.find('#Link').val();
   				var descIt = self.$editForm.find('#Info').val();
   				var typeIt = self.$editForm.find('#Type option:selected').val();
   				self.$edited.children('.title').text(titleIt);
   				self.$edited.children('.link').text(linkIt);
   				self.$edited.children('.desc').text(descIt);
   				self.$edited.children('.type').text(typeIt);
   				self.$window.dialog('close');
       		},
       		
       		_bindInsertData: function(){
       			var self = this;
       			this.$editForm.submit( function(){
       				if(self.$edited != null) self.changeOldData(self);
           			else self.createNewItem(self);
           			return false;
       			});
       		},
       	
       		deleteData: function(elem){
       			$(elem).parent('li').remove();
       		},
       		resetData: function(){
       			this.$editForm.get(0).reset();
       		},
       		
       		_bindSortableList: function(){
       			this.$listBody.sortable();
       		},
       		
       		_bindAddNewData: function(){
       			var self = this;
       			$('#addNewData').click(function(){
       				self.resetData();
       				self.$window.dialog('open');
       				self.$window.dialog({'title':'Dodaj'});
       				self.$edited = null;
       			});
       		}
       		
       });