	var currentSlide = 1;
	var slideSize = 0;
	var $slidesHTML = $();
	
		$(document).ready(function(){
			
			
			CKEDITOR.replace( 'slideText',{
				width : 900, 
				height: 300,
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
			//ładuje  zawartość działów 
			changeDepartmentSelect();
			//ustawienie początkowej wartości w select działu
			var $depart = $('#departmentThemeHidden');
			if($depart.val() != "") {
				$('#departmentTheme').children('option').each(function(){
					$opt = $(this);
					if( $opt.val() == $depart.val()){
						$opt.attr('selected','selected');
					}
				});
			}
				
			$slidesHTML = $('#slidesHTML');
			slideSize = $slidesHTML.children('section').length;
			if(slideSize == 0) {
				$slidesHTML.append('<section class="slide" id="slide-1" >Dodaj</section>');
				slideSize = 1;
				currentSlide = 1;
			}
			
			createPage();
		});
		
		function createPage(){
			var slideStr = '#slide-' + currentSlide.toString();
			var $slideSection = $(slideStr);
			
			//wstawiam treść akutalnego slajdu
			CKEDITOR.instances.slideText.setData($slideSection.get(0).innerHTML.toString());
			var $slideCh = $('#slideChoice');
			//usuwam wszystkie elementy listy (wyboru slajdu) 
			$slideCh.children().remove();
			
			//dodaje elementy listy wyboru podświetlajac aktualny
			for(i = 1; i <= slideSize; i++){
				$slideCh.append('<li>' + i.toString() + '</li>');
				if(i == currentSlide) $slideCh.children().last().addClass('highlightLi');
			} 
			$slideCh.children().click(function() {
				insertFromCKEditor();
				var $ul = $(this);
				currentSlide = parseInt($ul.text());
				createPage();
			}); 
		}
		
		function insertFromCKEditor() {
			var slideStr = '#slide-' + currentSlide.toString();
			$(slideStr).empty().append(CKEDITOR.instances.slideText.getData());
		}
		
		function insertToCKEditor() {
			var slideStr = '#slide-' + currentSlide.toString();
			CKEDITOR.instances.slideText.setData($(slideStr).get(0).innerHTML);
		}
		
		function addSlide(){
			insertFromCKEditor();

			var isAfter = ($('#radioAfter').attr('checked') == 'checked');
			
			var slideStr = '#slide-' + currentSlide.toString();
			if (isAfter) {
				$(slideStr).after('<section class="slide" ></section>');
				currentSlide++;
			}
			else $(slideStr).before('<section class="slide" ></section>');
			slideSize++;
			makeOrderIdSections();
			createPage();
		}
		
		function delSlide(){
			if (slideSize == 1) {
				alert("Nie można usunąć ostatniego slajdu");
				return;
			}
			if(confirm('Na pewno chcesz usunąć slajd nr ' + (currentSlide).toString() + ' ?')){
				
				var slideStr = '#slide-' + currentSlide.toString();
				$(slideStr).remove();
				makeOrderIdSections();
				slideSize--;
				if (currentSlide > slideSize) currentSlide = slideSize;
				createPage();
			}
		}
		//funkcja pomocnicza
		function makeOrderIdSections(){
			$slidesHTML.children('section').each(function(index) {
				$(this).attr('id','slide-' + (index+1));
			});
		}
		
		function changeDepartmentSelect(){
			var nrSub = $('#subjectTheme').children('option:selected').val();
			var $depTheme = $('#departmentTheme');
			$depTheme.children().remove();
			var themes = departmentData[nrSub];
			for(i in themes){
				$depTheme.append('<option value="'+ themes[i] +'">'+ themes[i] +'</option>');
			}
			//$depTheme.children('option').first().attr('selected','selected');
			
		}
		
		
		function createData(){
			if(!isValid(document.getElementById('save'))) return false;
			
			//zapisuje ostatnio edytowany slajd
			insertFromCKEditor();
			var dep = $('#departmentTheme').children('option:selected').val();
			$('#departmentThemeHidden').val(dep);
			var data = $slidesHTML.get(0).innerHTML;
			$('#slidesData').val(data);
			return true;
		}
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
		