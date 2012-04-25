
jQuery.showIcon = false;
  jQuery.fn.switchIcon = function() {
	  if(this.showIcon) {
		  this.attr('src','/images/addico.png');
		  this.showIcon = false;
	  }
	  else {
		  this.attr('src','/images/delico.png');
		  this.showIcon = true;
	  }
  }
  
  function EditAction($Div){
	  this.imgB = $Div.children('img');
	  var imgB = this.imgB;
	  this.form = $Div.children('form');
	  var form = this.form;
	  this.data = $Div.children('div');
	  var data = this.data;
	  //to override
	  this.setDataToForm = function(){
		  //alert("insertdata");
	  }
	  //to override
	  this.getDataFromForm = function(){
		  //alert("reinsertdata");
	  }
	  var self = this;
 	 this.toggleForm = function(){
		  form.toggle(100);
		  imgB.switchIcon();
		  if(imgB.showIcon){
			  self.setDataToForm();
		  }
	  }
 	  this.mkToggleForm = function(){
 		 self.toggleForm();
 	  }
 	  this.saveEdit = function(){ 
 		  self.getDataFromForm();
 		  self.toggleForm();
 		  }
 	  
	  this.init = function(){
		  imgB.showIcon = false;
		  imgB.click(this.mkToggleForm);
		  form.hide();
		  form.children().children('.saveButton').click(this.saveEdit)
	  }
	 
	  
  }
  
  function getPathWithoutIdChapter(){
    var path = window.location.toString();
    var pathArray = path.split('/');
    
    var last = pathArray[pathArray.length - 1];
    var toReturn = pathArray.join('/');
    if (last.length < 5) toReturn = pathArray.slice(0,pathArray.length - 1);
    return toReturn;
  }
  
  $(document).ready(function(){
	  
	  //-------------book info 
   var headerBook = new EditAction($('#bookHeader'));
   headerBook.setDataToForm = function(){
	  var title = headerBook.data.children('h1').first().text();
	  var info = headerBook.data.children('p').first().text();
	  headerBook.form.children().children('input:text').first().val(title);
	  headerBook.form.children().children('textarea').first().val(info);
   }
   headerBook.getDataFromForm = function(){
	   var title = headerBook.form.children().children('input:text').first().val();
	   var info = headerBook.form.children().children('textarea').first().val();
	   headerBook.data.children('h1').first().text(title);
	   headerBook.data.children('p').first().text(info);
   }
   headerBook.init();
   
   
 
  //------------permission show hide
   
  var permission = new EditAction($('#editPermission')); 
   permission.getDataFromForm = function(){
	  var userMail = permission.form.children('fieldset').children('input:text').first().val();
	  alert(userMail);
	  permission.data.children('ul').val();//change
   }
   
   permission.init();
  
    
    //---------------------newChapter object
     var newChapter = new Object();
      newChapter.imgB = $('#addNewChapterImg');
      newChapter.hForm = $('#formNewChapter');
      newChapter.tree = $('#ebookTree');
      newChapter.show = false;
      newChapter.switchForm = function(){
        newChapter.hForm.toggle(100);
       if(newChapter.show) { 
        newChapter.imgB.attr('src','/images/addico.png');
        newChapter.show = false;
     }
     else {
        newChapter.imgB.attr('src','/images/delico.png');
        newChapter.hForm.get(0).reset();
        newChapter.show = true;
     }
      }
      newChapter.insertAndClose = function(){
        var index = $('#chapterIndex').val();
        var title = $('#chapterTitle').val();
        var location = getPathWithoutIdChapter();
        var size = newChapter.tree.children('li').size();
        if (index > size) {
          newChapter.tree.children('li').last().after('<li><a href="' + location + '/' + size + '">' + title + '</a></li>');
        }
        else {
        newChapter.tree.children('li').each(function(i){
            if (i + 1 == index) {
              $(this).before('<li><a href="' + location + '/' + index + '">' + title + '</a></li>');
            }
          });
        }
        newChapter.switchForm();
        return true;
      }
      newChapter.init = function(){
        newChapter.imgB.click(newChapter.switchForm);
        newChapter.hForm.hide();
        $('#saveAddChapter').click(newChapter.insertAndClose);
      }
    newChapter.init();
    
   // --------------------- subchapter edit 
  
   var chapterEdit = new Object(); 
    chapterEdit.imgB = $();
    chapterEdit.form = $('#formEditChapter');
    chapterEdit.save = $('#saveChapter');
    chapterEdit.saveForm = function() {
      
    }
    chapterEdit.init =  function(){
      chapterEdit.form.hide();
    }
   
   chapterEdit.init();
   
   //--------- testing -------------
  
  
   
   
  });