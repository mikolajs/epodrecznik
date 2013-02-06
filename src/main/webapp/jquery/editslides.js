	
	function Slides(){
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
	
	
	
	
	function Properties(){
		this.$departInput = $('#departmentThemeHidden');
		//var $departInput = this.$departInput;
		this.$departSelect = $('#departmentTheme');
		//var $departSelect = this.$departSelect;
		this.$subjectSelect = $('#subjectTheme');
		//var $subjectSelect = this.$subjectSelect;
		var self = this;
		
		this.init = function(){
			var idSub = self.$subjectSelect.children('option:selected').val();
			self.$departSelect.children().hide();
			self.$departSelect.children('#'+idSub).show();
			var idDep = self.$departInput.val();
			if(idDep != "") {
				self.$departSelect.children('optgroup:visible').children().each(function(){
					var $opt = $(this);
					if($opt.val() == idDep) $opt.attr('selected','selected');
				});
			}
			else self.$departSelect.children('optgoup:visible').children().first().attr('selected','selected');
			self.refreshDepartmentInput();
		    self.$subjectSelect.change(self.refreshDepartmentSelect);
			self.$departSelect.change(self.refreshDepartmentInput);
		}
		
		this.refreshDepartmentSelect = function() {
			var idSub = self.$subjectSelect.children('option:selected').val();
			//alert(idSub);
			self.$departSelect.children('optgroup').hide();
			var idDep = $('#'+idSub).show().children().first().attr('selected','selected').val();
			// self.$departSelect.children('optgroup').children().first().val();
			self.$departInput.val(idDep);
		}
		
		this.refreshDepartmentInput = function(){
			var idDep = self.$departSelect.children('optgroup').children('option:selected').val();
			self.$departInput.val(idDep);
		}
		
	}
	
		$(document).ready(function(){
			
			
			CKEDITOR.replace( 'slideText',{
				width : 990, 
				height: 650,
				extraPlugins : 'youTube,addImage,formula',
        		toolbar : [ [ 'Source' ],
        		            [ 'Link','Unlink','Anchor' ],[ 'Maximize', 'ShowBlocks','-','About' ] ,
        		[ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ],
        		[ 'Find','Replace','-','SelectAll' ],
        		[ 'Bold','Italic','Underline','Strike','Subscript','Superscript','-','RemoveFormat' ],
        		[ 'NumberedList','BulletedList','-','Outdent','Indent','-','Blockquote','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock','-','BidiLtr','BidiRtl' ],
        		[ 'AddImage', 'YouTube', 'Formula', 'Table','HorizontalRule','SpecialChar','Iframe' ] ,
        		[ 'Styles','Format','Font','FontSize' ],
        		[ 'TextColor','BGColor' ] ]
    		});
			
			CKEDITOR.config.setStyles = [];
			CKEDITOR.config.contentsCss = '/deckjs/themes/style/neon_ckeditor.css';
			CKEDITOR.config.disableNativeSpellChecker = false;
			
			var properties = new Properties();
			properties.init();
			
			var slides = new Slides();
			slides.init();
			
			
		});
					
		
		//get url from iframe in CKEditor and insert it in url input in window massage
		function getImageURLfromIFrame(elem){
			var innerDoc = elem.contentDocument || elem.contentWindow.document;
			var url  = innerDoc.getElementById('path').value;
			$('.cke_dialog_ui_input_text').val(url);
		}
		
		//for ascii to math
		//var formula = document.getElementById("formulaEditor");
		translateOnLoad=false;
		function displayFormula() {

		  var str = document.getElementById("formulaEditor").value;
		  str = "`" + str + "`";
		  var outnode = document.getElementById("formulaDisplay");
		  var n = outnode.childNodes.length;
		  for (var i=0; i<n; i++)
		    outnode.removeChild(outnode.firstChild);
		  outnode.appendChild(document.createTextNode(str));
		  AMprocessNode(outnode);
		}
		//end ascii to math
		