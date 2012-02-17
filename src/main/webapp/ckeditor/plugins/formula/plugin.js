

CKEDITOR.plugins.add( 'formula',
          {
            init: function( editor )
            {  
              editor.addCommand( 'formula', new CKEDITOR.dialogCommand( 'formulaDialog' ));
        
              editor.ui.addButton( 'Formula',
              {
                label: 'Wczytaj obrazek',
                command: 'formula',
                icon: this.path + 'image.png'
              } );
        
        CKEDITOR.dialog.add( 'formulaDialog', function( editor )
    {
      return {
        title : 'Edytor wzorów',
        minWidth : 400,
        minHeight : 300,
        contents :
        [
          {
            id : 'tab1',
            label : 'Edycja wzorów',
            elements :
            [
	      {
	       type : 'html',
	       html : '<textarea id="formulaEditor" onkeyup="displayFormula(this)" class="cke_dialog_ui_input_textarea"></textarea>'
	      },
	      {
	       type : 'html',
	       html : '<div id="formulaDisplay"></div>'
	      }
            ]
          },
          
        ],
	
        onOk : function()
        {
	  var imageNode = null;
				if ( !this.fakeImage )
				{
					imageNode = CKEDITOR.dom.element.createFromHtml( '<cke:img></cke:img>', editor.document );
				}
				else
				{
					imageNode = this.iImageNode;
				}
				
				 var dialog = this;
		      var url = dialog.getValueOf('tab1','url');
			imageNode.setAttribute('src',url);
				var newFakeImage = editor.createFakeElement( imageNode, 'cke_img', 'img', true );
				
				newFakeImage = newFakeImage.setAttribute('src',url);
				if ( this.fakeImage )
				{
					newFakeImage.replace( this.fakeImage );
					editor.getSelection().selectElement( newFakeImage );
				}
				else
					editor.insertElement( newFakeImage );
        
        },
       
      };
    } );
               
             
            }
          } );
 
