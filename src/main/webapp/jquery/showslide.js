
   $(document).ready(function() {
    	
        $.deck('.slide'); 
        var arrayId = new Array();
       
        $('#slides').children('section').each(function() {
            arrayId.push('#' + this.id)
        });
    $.deck(arrayId); 
    $('#next-slide').click(function() {
            $.deck('next');
        }); 
    $('#previous-slide').click(function() {
            $.deck('prev');
        }); 
      //$('#slides').click(function() {
       //     $.deck('next');
       // });   
//      var h = window.innerHeight - 60;
//      $('#slides').css('min-height', h + 'px' );
//     
      $.extend(true, $.deck.defaults, {
        selectors: {
        statusCurrent: '.deck-status-current',
        statusTotal: '.deck-status-total'
       },
        countNested: true
        });
 
     if(!isFirefox()){
    	 if(isOld()) alert("Zawartość może nie wyglądać prawidłowo. Użyj nowszej przeglądarki, najlepiej Firefox.");
    	 else {
    		if( $('math').length > 0){
    			alert("Prezentacja może zawierać wzory. Ich obsługa wymaga użycia przeglądarki Firefox");
    		}
    	 }
     }
    });  



    var styleId = 0;
    function changeTemplate(){
    	var link = document.getElementById('style-theme-link');
    	switch(styleId) {
    	case 0: 
    		link.setAttribute('href', '/deckjs/themes/style/swiss.css');
    		styleId = 1;
    	    break;
    	case 1: 
    		link.setAttribute('href', '/deckjs/themes/style/web-2.0.css');
    		styleId = 2;
    	    break;
    	case 2: 
    		link.setAttribute('href', '/deckjs/themes/style/neon.css');
    		styleId = 0;
    	    break;
    	default:
    	    break;
    	}
    }
    
    
    function isMSIE(){
    	var userAgent = navigator.userAgent;
    	var msie = userAgent.match(/MSIE/gi);
    	if(msie != null && msie.length > 0) return true;
		else return false;
    }
    
    function goFull(){
    	 var elem = document.getElementById('thebody');
    	 if (elem.mozRequestFullScreen) {
    	      elem.mozRequestFullScreen();
    	      _makeFull();
    	    } else if (element.webkitRequestFullScreen) {
    	      elem.webkitRequestFullScreen();
    	      _makeFull();
    	   }
    }
    
    function _makeFull(){
    	 var h = parseFloat(window.screen.height) - 40.0;
    	 var w = parseFloat(window.screen.width);
    	 var prop = 0.0;
    	 if(w/h < 1.65) {
    		 prop = (w/1000.0);
    	 }
    	 else {
    		 prop = (h/610.0) ;
    	 }
    	 var translateY = parseInt(h/2)-270;
    	 alert(translateY);
    	 $('#slides').css('transform', 'scale(' + prop + ', ' + prop + ')' ).css('top', translateY+'px');
    }
    
    
    
    function showDetails(){
    	var current = $('.deck-status-current').get(0).innerHTML;
    	var content = $('#details-'+current).get(0).innerHTML;
    	$('#detailsShow').children('details').html(content);
    	$('#detailsShow').dialog( "option", "width", 900 );
    	$('#detailsShow').dialog( "option", "height", 600 );
    	$('#detailsShow').dialog( "open" );
    }
    
    
    function isFirefox(){
    	var userAgent = navigator.userAgent;
    	var n = userAgent.match(/Firefox/gi);
		if(n != null && n.length > 0) return true;
		else return false;
    }
    
    function isOld(){
    	var userAgent = navigator.userAgent;
    	var msie = userAgent.match(/MSIE .\../gi);
    	if (msie != null){
    		var n=msie.match(/MSIE \d+\../gi);
        	var w = "";
        	if (n.lenght > 0){
        		w = n[0];
        		w = w.split(' ')[1].split('.')[0];
            	if (parseInt(w) > 8) return false;
            	else return true;
        	}
    	}
    	
    	n = userAgent.match(/Chrome/gi);
    	if(n != null) return false;
    	n = userAgent.match(/Safari/gi);
    	if(n != null) return false;
    	n = userAgent.match(/Opera/gi);
    	if(n != null) return true;
    	
    	return true;	
    }
    
 
