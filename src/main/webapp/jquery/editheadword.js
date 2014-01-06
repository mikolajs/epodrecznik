	
	var EditHeadWord = dejavu.Class.declare({
		
		slideMaxNr : 4,
		$slidesHTML : null,
		$slideChoice : null,
		slideSize : 0,
		$listItem : null,
		
		initialize : function(maxSize) {
			this.slideMaxNr = maxSize;
			this.$slidesHTML = $('#headWordsData');
			this.slideSize = this.$slidesHTML.children('section').length;
			this.createPage();
			
			$('#save').click(function(){self.createData();});
			$('#addSlideAction').click(function(){self.addSlideAction();});
			$('#delSlideAction').click(function(){self.delSlide();});	
			
			if(this.slideSize == 0) this.slideSize = 1;
			for( i = this.slideSize +1;  i <  5;  i++){
		    	$('#section_'+ i).hide();
		    	$('#slideInfo_'+ i).hide();
		    }		  
		},
		
		createPage : function(){	
			var self = this;
		    this.$slidesHTML.children('section').each(function(index){
				var $this = $(this);
				var html = $('#section_'+ index).html($this.html());
			}); 	
		},
		
		addSlideAction : function(){
			if(this.slideMaxNr <=  this.slideSize) {
				alert("Maksmalna ilość slajdów to: " + this.slideMaxNr);
				return;
			}
			$("#section_"+ this.slideSize +1 ).show();
			$('#slideInfo_'+ his.slideSize +1).show();
			 this.slidexSize++;
		},
		
		delSlide : function(){
			if (this.slideSize == 1) {
				alert("Nie można usunąć ostatniego slajdu, skasuj całe hasło.");
				return;
			}
			if(confirm('Na pewno chcesz usunąć slajd nr ' + this.slideSize + ' ?')){
				$("#section_"+ this.slideSize ).html("").hide();
				$("#slideInfo_"+ this.slideSize ).hide();
				this.slideSize--;
			}
		},
			
		createData :  function(){
			if(!isValid(document.getElementById('save'))) return false;
			var dataSlides = "";
			for(i = 1; i < this.slideSize; i++){
				var html = $('#section_'+i).html();
				dataSlides += html.toString();
			}
			this.$slidesHTML.val(dataSlides);
			return true;
		}
		
	});
	
	
	
	
	
		
					
		