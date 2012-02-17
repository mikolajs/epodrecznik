

CKEDITOR.plugins.add( 'addImage',
          {
            init: function( editor )
            {  
              editor.addCommand( 'addImage', new CKEDITOR.dialogCommand( 'addImageDialog' ));
        
              editor.ui.addButton( 'AddImage',
              {
                label: 'Wczytaj obrazek',
                command: 'addImage',
                icon: this.path + 'image.png'
              } );
        
        CKEDITOR.dialog.add( 'addImageDialog', function( editor )
    {
      return {
        title : 'Dodaj obrazek',
        minWidth : 400,
        minHeight : 300,
        contents :
        [
          {
            id : 'tab1',
            label : 'Obrazek',
            elements :
            [
              {
                type : 'text',
                id : 'url',
                label : 'Url do obazka',
                validate : CKEDITOR.dialog.validate.notEmpty('Nie może być pusty')
              },
	      {
	       type : 'html',
	       html : '<iframe src="/imagestorage" width="390px" height="250px" frameborder="0" onload="getImageURLfromIFrame(this)" ></iframe>'
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
 