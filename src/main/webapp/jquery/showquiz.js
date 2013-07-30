

var ShowQuiz = dejavu.Class.declare({
	questNr : 1,
	questCorrect : 0,
	questNow : 1, 
	
	initialize : function() {
	    this.questNr = parseInt($('#questionsNr').text());
	   
        this._refreshPercentInfo();
        var answerContent = $('#answerContent');
		$('#answerCloud').children('ul').children('li').each(function(){
			$(this).click(function(){
				answerContent.val(this.innerHTML);
			});
		});
	},
	
	afterAnswered : function(wasGood) {
		if(wasGood) this.questCorrect++;
		
		this.questNow++; 
		$('#answerContent').val("");
		var answerInfo = "Błędna odpowiedź";
		if(wasGood) answerInfo = "Odpowiedź prawidłowa";
		alert(answerInfo);
		this._refreshPercentInfo();
	},
	
	beforeAnswer : function() {
		//validate
	},
	
	_refreshPercentInfo : function () {
		var percentCorrect = this._countCorrectPercentAI();
        this._setNumberAnswerInfo();
        this._setPercentAnswerInfo(percentCorrect);
        this._setCanvasIndicator(percentCorrect); 
	},
	
	_setCanvasIndicator : function(percentCorrect) {
	     var canvas = document.getElementById('canvasAnswerInfo');
	     var context = canvas.getContext('2d');
	     var w = canvas.width;
	     var h = canvas.height;
	     var bad = (h* (100-percentCorrect)) /100;
	     context.fillStyle = '#33ee33';
	     context.fillRect(0,0,w,h);
	     context.fillStyle = '#ee3333';
	     context.fillRect(0,0,w,bad);
	},
	
	_setNumberAnswerInfo : function() {
		if (this.questNow <= this.questNr)
	     $('#questNumberAnswerInfo').text(this.questNow.toString() + '/' + this.questNr.toString());
	},
	
	_setPercentAnswerInfo : function(percentCorrect) {
		$('#percentAnswerInfo').text(percentCorrect.toString() +  " %");
	},
	
	_countCorrectPercentAI : function() {
	   if(this.questNow == 1) return 100;
	   else {
		   var proportion = parseFloat(this.questCorrect)/parseFloat(this.questNow - 1);
		   return (parseInt(proportion * 100.0));
	   }
		  
	}
	
});
