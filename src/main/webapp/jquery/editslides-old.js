	
	function Slides(slideMaxNr){
		this.slideMaxNr = slideMaxNr;
		this.$slidesHTML = $('#slidesHTML');
		this.$slidesDataInput = $('#slidesData');
		this.$slideChoice = $('#slideChoice');
		this.slideSize = 0;
		this.currentSlide = 1;
		var self = this;
		this.insertFromCKEditor = function(){
			var slideStr = '#slide-' + self.currentSlide.toString();
			$(slideStr).empty().append(CKEDITOR.instances.slideText.getData());
		}
		
		this.insertToCKEditor = function() {
			var slideStr = '#slide-' + self.currentSlide.toString();
			CKEDITOR.instances.slideText.setData($(slideStr).get(0).innerHTML);
		}
		
		this.init = function() {
			self.slideSize = self.$slidesHTML.children('section').length;
			if(self.slideSize == 0){
				self.$slidesHTML.append('<section class="slide" id="slide-1" >Dodaj</section>');
				self.slideSize = 1;
				self.currentSlide = 1;
			}
			self.createPage();
			$('#save').click(self.createData);
			$('#addSlideAction').click(self.addSlide);
			$('#delSlideAction').click(self.delSlide);
			
		}
		
		this.choiceSlide = function(){
			self.insertFromCKEditor();
			var $ul = $(this);
			self.currentSlide = parseInt($ul.text());
			self.createPage();
		}
		this.createPage = function(){
			self.insertToCKEditor();
			//usuwam wszystkie elementy listy (wyboru slajdu) 
			self.$slideChoice.children().remove();
			//dodaje elementy listy wyboru podświetlajac aktualny
			for(i = 1; i <= self.slideSize; i++){
				self.$slideChoice.append('<li>' + i.toString() + '</li>');
				if(i == self.currentSlide) self.$slideChoice.children().last().addClass('highlightLi');
			} 
			self.$slideChoice.children().click(self.choiceSlide); 
		}
		
		this.addSlide = function(){
			if(self.slideMaxNr <= self.slideSize) {
				alert("Maksmalna ilość slajdów to: " + self.slideMaxNr);
				return;
			}
			self.insertFromCKEditor();

			var isAfter = ($('#radioAfter').attr('checked') == 'checked');
			
			var slideStr = '#slide-' + self.currentSlide.toString();
			if (isAfter) {
				$(slideStr).after('<section class="slide" ></section>');
				self.currentSlide++;
			}
			else $(slideStr).before('<section class="slide" ></section>');
			self.slideSize++;
			self.makeOrderIdSections();
			self.createPage();
		}
		
		this.delSlide = function(){
			if (self.slideSize == 1) {
				alert("Nie można usunąć ostatniego slajdu");
				return;
			}
			if(confirm('Na pewno chcesz usunąć slajd nr ' + (self.currentSlide).toString() + ' ?')){
				
				var slideStr = '#slide-' + self.currentSlide.toString();
				$(slideStr).remove();
				self.makeOrderIdSections();
				self.slideSize--;
				if (self.currentSlide > self.slideSize) self.currentSlide = self.slideSize;
				self.createPage();
			}
		}
		//funkcja pomocnicza
		this.makeOrderIdSections = function(){
			self.$slidesHTML.children('section').each(function(index) {
				$(this).attr('id','slide-' + (index+1));
			});
		}
		this.createData =  function(){
			if(!isValid(document.getElementById('save'))) return false;
			
			//zapisuje ostatnio edytowany slajd
			self.insertFromCKEditor();
			//var dep = $('#departmentTheme').children('option:selected').val();
			//$('#departmentThemeHidden').val(dep); //to robi na bieżąco properties
			var data = self.$slidesHTML.get(0).innerHTML;
			$('#slidesData').val(data);
			return true;
		}
		
	}
	
	
	
	
	
		
					
		