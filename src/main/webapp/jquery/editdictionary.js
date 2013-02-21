	
		var $slideData = $();

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
        		[ 'Styles' ],
        		[ 'TextColor','BGColor' ] ]
    		});
			
			CKEDITOR.config.setStyles = [];
			CKEDITOR.config.contentsCss = '/deckjs/themes/style/neon_ckeditor.css';
			CKEDITOR.config.disableNativeSpellChecker = false;
			
			$contentData = $('#contentData');
			CKEDITOR.instances.slideText.setData($contentData.val());
			
			
			$('#save').click(function(){
			  var entry = $('#entry').val();
			  if(jQuery.trim(entry) == "") {
				  alert("Uzupełnij hasło");
				  return false;
			  }
			  $contentData.val( CKEDITOR.instances.slideText.getData());
			  alert($contentData.val());
			  return true;
			})
			//var properties = new Properties();
			//properties.init();
			
			//var slides = new Slides();
			//slides.init();
			
			
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
		