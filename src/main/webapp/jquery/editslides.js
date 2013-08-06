	
	var EditSlides = dejavu.Class.declare({
		
		slideMaxNr : 30,
		$slidesHTML : null,
		$slideChoice : null,
		$currentSlide : null,
		slideSize : 0,
		$listItem : null,
		
		initialize : function(maxSize) {
			this.slideMaxNr = maxSize;
			this.$slidesHTML = $('#slidesHTML');
			this.slideSize = this.$slidesHTML.children('section').length;
			this.$slideChoice = $('#slideChoice');
			if(this.slideSize == 0){
				this.$slidesHTML.append('<section class="slide">Dodaj</section>');
				this.slideSize = 1;
			}
			this.createPage();
			var self = this;
			$( "#slideChoice" ).sortable({
				stop: function( event, ui ) {
					$('#slideChoice').children('div.lista').each(function(index){
					$(this).children('span').get(0).innerHTML = (index + 1).toString();
				});}
			});
			$('#save').click(function(){self.createData();});
			$('#addSlideAction').click(function(){self.addSlide();});
			$('#delSlideAction').click(function(){self.delSlide();});
			
		},
		
	    insertFromCKEditor : function(){
	    	var data = CKEDITOR.instances.slideText.getData();
	    	this.$listItem.children('section').get(0).innerHTML = data;
		},
		
		insertToCKEditor : function() {
			var data = this.$listItem.children('section').html();
			CKEDITOR.instances.slideText.setData(data);
		},
		
		choiceSlide : function(elem){
			this.insertFromCKEditor();
			this.$listItem = $(elem);
			this._setNewHighlightItem();
			this.insertToCKEditor();
		},
		
		_setNewHighlightItem : function() {
			this.$slideChoice.children('div.lista').removeClass('highlightLi'); 
			this.$listItem.addClass('highlightLi');
		}, 
		
		createPage : function(){
						
			this.$slideChoice.children().remove();
			var self = this;
			var allSections = this.$slidesHTML.children('section').each(function(index){
				var $this = $(this);
				self.$slideChoice.append('<div class="lista"><span>' + (index + 1).toString()+ '</span>'
						+ '<section>' + $this.get(0).innerHTML + '</section>' + '</div>');
			});
			this.$slideChoice.children('div.lista').children('section').each(function() {
				$(this).hide();
			});
				
			this.$listItem = this.$slideChoice.children('div.lista').first();
			this.$listItem.addClass('highlightLi');
			
			this.insertToCKEditor();
			var self = this;
			this.$slideChoice.children('div.lista').click(function() {self.choiceSlide(this)}); 
		},
		
		addSlide : function(){
			if(this.slideMaxNr <= this.slideSize) {
				alert("Maksmalna ilość slajdów to: " + this.slideMaxNr);
				return;
			}
			this.insertFromCKEditor();

			var isAfter = ($('#radioAfter').attr('checked') == 'checked');
			
			var itemString = '<div class="lista"><span>x</span><section style="display:none;"></section></div>';
			if (isAfter) {
				this.$listItem.after(itemString);
				this.$listItem = this.$listItem.next();
			}
			else {
				this.$listItem.before(itemString);
				this.$listItem = this.$listItem.prev();
			}
			this.slideSize++;
			this.insertToCKEditor();
			this._renumberItems();
			this._setNewHighlightItem();
			var self = this;
			this.$listItem.click(function(){self.choiceSlide(this);});
		},
		
		delSlide : function(){
			if (this.slideSize == 1) {
				alert("Nie można usunąć ostatniego slajdu");
				return;
			}
			var nrStr = jQuery.trim(this.$listItem.children('span').text());
			if(confirm('Na pewno chcesz usunąć slajd nr ' + nrStr + ' ?')){
				var newListItem = null;
				if(nrStr == "1") newListItem = this.$listItem.next();
				else newListItem = this.$listItem.prev();
				this.$listItem.remove();
				this.$listItem = newListItem;
				this.slideSize--;
				this.insertToCKEditor();
				this._renumberItems();
				this._setNewHighlightItem();
			}
		},
		
		_renumberItems : function() {
			this.$slideChoice.children('div.lista').each(function(index){
				$(this).children('span').get(0).innerHTML = (index + 1).toString();
			});
		},
		
		
		createData :  function(){
			if(!isValid(document.getElementById('save'))) return false;
			
			//zapisuje ostatnio edytowany slajd
			this.insertFromCKEditor();
			var data  = "";
			//var dep = $('#departmentTheme').children('option:selected').val();
			//$('#departmentThemeHidden').val(dep); //to robi na bieżąco properties
			this.$slideChoice.children('div.lista').each(function(index){
				var inner = $(this).children('section').get(0).innerHTML ;
				data +=  '<section id="slide-' + (index +1).toString()
					+ '">' + inner +  '</section>';
			});
			$('#slidesData').val(data);
			return true;
		}
		
	});
	
	
	
	
	
		
					
		