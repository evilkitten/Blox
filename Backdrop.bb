
Const BACKDROPTEXTUREFLAGS=777
;Const BACKDROP_TEXTURENAME$="background.bmp"

Const BACKDROP_DISTANCE_OFFSET#=5.0

;Const MIRROR_REFRACTICVE_INDEX#=0.8

Const BACKDROP_TEXTURESCALE#=4.0
Const BACKDROP_TEXTUREWIDTH=32
Const BACKDROP_TEXTUREHEIGHT=32
Const BACKDROP_TEXTUREFIRSTFRAME=0
Const BACKDROP_TEXTURECOUNTFRAME=18

Global BACKDROP
Global CURRENTBACKDROPTEXTURE
Global CURRENT_BACKDROP_FRAME

Global BACKDROP_COL

;Global MIRROR

Function ChangeBackDropTexture()
	GetBackdropData
	ScaleTexture CURRENTBACKDROPTEXTURE,BACKDROP_TEXTURESCALE,BACKDROP_TEXTURESCALE
	EntityTexture BACKDROP,CURRENTBACKDROPTEXTURE,0,0
	FreeTexture CURRENTBACKDROPTEXTURE
	CURRENTBACKDROPTEXTURE=0
End Function

Function GetBackdropData()
	Local ColourChoice
	Local Swap
	
	If (STATE=STATE_GAME) And (ROUND<COLOURS_SELECTION)
		;Ensure correct backdrops for initial levels in Game mode. After this, or for Attract Mode, randomise backdrop
		ColourChoice=(ROUND Mod COLOURS_SELECTION)+1
		Swap=0
	Else
		ColourChoice=Rand(COL_SELECTION_RED,COLOURS_SELECTION)
		Swap=Rand(0,1)
	End If
	
	BACKDROP_COL=ColourChoice
	
	Local Light=GetLightColour(ColourChoice)
	Local Dark=GetDarkColour(ColourChoice)
	
	If (Swap)
		Swap=Light
		Light=Dark
		Dark=Swap
	End If
	
	Restore BD_
	
	Local Pass
	Local Bits
	
	Local Pixel
	
	For Pass=0 To CURRENT_BACKDROP_FRAME-1
		For Bits=0 To (BACKDROP_TEXTUREHEIGHT*2)-1
			Read Pixel
		Next
	Next
	
	Pass=0
	Bits=0
	
	Local Y
	Local X
	Local Byte
	
	If (CURRENTBACKDROPTEXTURE)
		FreeTexture CURRENTBACKDROPTEXTURE
	End If
	
	CURRENTBACKDROPTEXTURE=CreateTexture(BACKDROP_TEXTUREWIDTH,BACKDROP_TEXTUREHEIGHT,BACKDROPTEXTUREFLAGS)
	
	Local Buffer=TextureBuffer(CURRENTBACKDROPTEXTURE)
	
	LockBuffer Buffer
	
	
		For Y= 0 To BACKDROP_TEXTUREHEIGHT-1
			For Pass=0 To 1
				Read Byte
				For X= 0 To (BACKDROP_TEXTUREWIDTH*0.5)-1
					Bits=(Byte And (2^X))
					If (Bits)
						Pixel=Dark
					Else
						Pixel=Light
					End If
					WritePixelFast(X+(Pass*BACKDROP_TEXTUREWIDTH*0.5),Y,Pixel,Buffer)
				Next
			Next
		Next
		
	UnlockBuffer Buffer
	
End Function
;~IDEal Editor Parameters:
;~F#16#1E
;~C#Blitz3D