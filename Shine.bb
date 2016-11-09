Const SHINE_DECREASE_RATE#=0.04
Const SHINETEXTURE_SIZE=32
Const SHINETEXTUREFLAGS=771
;Const SHINETEXTURENAME$="shine.png"
Const SHINE_FX=9

Const SHINE_DEF_COLOUR=-1
Const SHINE_DEF_TRANSPARENCY#=1.0

Global SHINETEXTURE

Type SHINE
	Field Entity
	Field Alpha#
End Type

Function CreateShine.SHINE(X#,Y#,W#=1.0,H#=1.0,Colour=SHINE_DEF_COLOUR,Alpha#=SHINE_DEF_TRANSPARENCY)
	Local S.SHINE=New SHINE
	S\Entity=CreateQuad()
	ScaleMesh S\Entity,W,H,1
	EntityFX S\Entity,9 
	EntityTexture S\Entity,SHINETEXTURE
	S\Alpha=Alpha
	EntityColor S\Entity,Colour And 255, (Colour Shr 8) And 255,(Colour Shr 16) And 255
	PositionEntity S\Entity,X,Y,GENERIC_Z_OFFSET,True
	Return S
End Function

Function UpdateShines()
	Local S.SHINE
	For S=Each SHINE
		S\Alpha=S\Alpha-SHINE_DECREASE_RATE
		If (S\Alpha>0.0)
			EntityAlpha S\Entity,S\Alpha	
		Else
			FreeEntity S\Entity
			Delete S
		End If
	Next
End Function
;~IDEal Editor Parameters:
;~F#B#10#1C
;~C#Blitz3D