	var currentSlajd = 0;
	
		$(document).ready(function(){
			
			CKEDITOR.replace( 'slajdText',{
				width : 950, 
				height:300,
        		toolbar : [ [ 'Source', '-', 'Bold', 'Italic', 'Underline', 'Strike' ],
        		[ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ],
        		[ 'Find','Replace','-','SelectAll' ],
        		[ 'Bold','Italic','Underline','Strike','Subscript','Superscript','-','RemoveFormat' ],
        		[ 'NumberedList','BulletedList','-','Outdent','Indent','-','Blockquote','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock','-','BidiLtr','BidiRtl' ],
        		[ 'Link','Unlink','Anchor' ],
        		[ 'Table','HorizontalRule','SpecialChar' ] ,
        		[ 'Styles','Format','Font','FontSize' ],
        		[ 'TextColor','BGColor' ],[ 'Maximize', 'ShowBlocks','-','About' ]  ]
    		});
			createPage();
			changeDepartmentSelect();
		});
		
		function createPage(){
			var countSlajds = slajdList.length;
			//jeśli nie ma slajdu dodaje pierwszy
			if (countSlajds == 0) {
				slajdList.push('');
				countSlajds++;
			} 
			//wstawiam treść akutalnego slajdu
			$('#slajdText').val(slajdList[currentSlajd]);
			var $slajdCh = $('#slajdChoise');
			//usuwam wszystkie elementy listy (wyboru slajdu) 
			$slajdCh.children().remove();
			
			//dodaje elementy listy wyboru podświetlajac aktualny
			for(i = 1; i <= countSlajds; i++){
				$slajdCh.append('<li>' + i.toString() + '</li>');
				if(i-1 == currentSlajd) $slajdCh.children().last().addClass('highlightLi');
			} 
			//bind click
			$slajdCh.children().click(function() {
				slajdList[currentSlajd] = CKEDITOR.instances.slajdText.getData();
				var $ul = $(this);
				currentSlajd = parseInt($ul.text()) - 1;
				createPage();
			}); 
			CKEDITOR.instances.slajdText.setData(slajdList[currentSlajd]);
		}
		
		function addSlajd(){
			slajdList[currentSlajd] = CKEDITOR.instances.slajdText.getData();
			var countSlajds = slajdList.length;
			var isAfter = ($('#radioAfter').attr('checked') == 'checked');
			if(isAfter){
				currentSlajd++;
				var firstArray = slajdList.slice(0,currentSlajd );
				firstArray.push('');
				slajdList = firstArray.concat(slajdList.slice(currentSlajd));
			}
			else {
				var firstArray = slajdList.slice(0,currentSlajd );
				firstArray.push('');
				slajdList = firstArray.concat(slajdList.slice(currentSlajd));
			}
			createPage();
		}

		function delSlajd(){
			if (slajdList.length == 0) {
				alert("Nie można usunąć ostatniego slajdu");
				return;
			}
			if(confirm('Na pewno chcesz usunąć slajd nr ' + (currentSlajd + 1).toString() + ' ?')){
				
				if (currentSlajd != 0){
					var firstArray = firstArray = slajdList.slice(0,currentSlajd-1);
					slajdList = firstArray.concat(slajdList.slice(currentSlajd));
				} 
				else {
					slajdList = slajdList.slice(1);
				}
				
				if (currentSlajd >= slajdList.length) currentSlajd--
				createPage();
			}
		}
		
		function changeDepartmentSelect(){
			var nrSub = $('#subjectTheme').children('option:selected').val();
			var $depTheme = $('#departmentTheme');
			$depTheme.children().remove();
			var themes = departmentData[nrSub];
			for(i in themes){
				$depTheme.append('<option value='+ themes[i] +'>'+ themes[i] +'</option>');
			}
		}
		
		function createDataXML(){
			if(!isValid(document.getElementById('save'))) return false;
			//zapisuje ostatnio edytowany slajd
			slajdList[currentSlajd] = CKEDITOR.instances.slajdText.getData();
			for(i in slajdList) slajdList[i].replace('000000000o0000000000o0000000000','ERROR')
			var data = slajdList.join('000000000o0000000000o0000000000');
			alert(data);
			$('#slajdsData').val(data);
			alert(data);
			return true;
		}
		