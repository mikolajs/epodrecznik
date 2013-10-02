
  
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
    	 var translateY = h/14;
    	 $('#slides').css('transform', 'scale(' + prop + ', ' + prop + ')' +' translateY('+ translateY +'px)' );
    }
    
    
    function showDetails(){
    	var current = $('.deck-status-current').get(0).innerHTML;
    	var content = $('#details-'+current).get(0).innerHTML;
    	$('#detailsShow').children('details').html(content);
    	$('#detailsShow').dialog( "option", "width", 900 );
    	$('#detailsShow').dialog( "option", "height", 600 );
    	$('#detailsShow').dialog( "open" );
    }
    
  
//  var rescaleSlide = function(){
// 	var $current = $('#slides').children('.deck-current');
//	    var H = $current.height();
//	 	var h = window.innerHeight - 40;
//	    var fSize = 1.2;
//	    var n = 20;
//	    if(H > h) {
//	    	while(H > h && n > 0) {
//	   	    	$current.children().each(function(){
//	   	    		if(this.tagName == "H1") this.style.fontSize = fSize * 1.8 + "em";
//	   	    		else if(this.tagName == "H2") this.style.fontSize = fSize * 1.4 + "em";
//	   	    		else this.style.fontSize = fSize + "em";
//	   	    	});
//	   	    	fSize -= 0.1; 
	   	    	
//	  		    $current.find('img').each(function(){
//	  		    		this.width =  this.width * 0.9;
//	  		    		this.height = this.height * 0.9;
//	  		    		//this.style.heigth = 'auto';??????????
//	  		    	});
//	  				$current.find('span').each(function(){
//	  		    		$span = $(this);
//	  		    		var fontSizeStyle = $span.css('font-size');
//	  					fontSizeStyle = fontSizeStyle.substring(0, fontSizeStyle.length - 2);
//	  					fontSizeStyle *= 0.9;
//	  					$span.css('font-size',fontSizeStyle.toString() + 'px');
//	  		    	});
	   	    	
//	   	    	H = $current.height();
//	   	    	n--;
//	   	    }
//	    } 
//	    else {
//	    	//add transform html5
//	    	$current.style.transform = "scale(0.4,0.4)";
//	    }
	    
//   }