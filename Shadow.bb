Const SHADOWTEXTUREFLAGS=771;770
;Const SHADOWTEXTURENAME$="shadow.png"
Const SHADOWTEXTURE_SIZE=32
Const SHADOW_OFFSET#=2.0
Const SHADOW_TRANSAPRENCY#=0.5
Const SHADOW_SIZE=4

Const SHADOW_Z_ALIGNMENT=1

Const SHADOW_ALIGNMENT_ANGLE#=180

Global SHADOWTEXTURE
Global BATSHADOWENT

;Global OBJECT_SHADOWS_ON

Type SHADOW
	Field Entity
End Type

Function CreateShadow.SHADOW(W,H,Parent)
	Local S.SHADOW=New SHADOW
	S\Entity=CreateQuad(Parent )
	RotateMesh S\Entity,0,SHADOW_ALIGNMENT_ANGLE,0
	ScaleMesh S\Entity,SHADOW_SIZE*W,SHADOW_SIZE*H,1
	EntityTexture  S\Entity,SHADOWTEXTURE
	EntityAlpha  S\Entity,SHADOW_TRANSAPRENCY
	PositionEntity S\Entity,EntityX(Parent,True)+SHADOW_OFFSET,EntityY(Parent,True)-SHADOW_OFFSET,GENERIC_Z_OFFSET+SHADOW_Z_ALIGNMENT,True
	;EntityBlend S\Entity,1
	
	Return S
End Function

Function FreeShadow(S.SHADOW)
	If (S<>Null)
		If S\Entity Then FreeEntity S\Entity
		Delete S
	End If
End Function

Function BatShadow()
	CreateShadow(BAT_LENGTHSIZE,BAT_THICKSIZE,BAT)
End Function

Function BallShadow(B.BALL)
	;If OBJECT_SHADOWS_ON
		B\Shadow.SHADOW=CreateShadow(BALL_SIZE,BALL_SIZE,B\Entity)
	;End If
End Function

Function UpdateShadows()
	Local S.SHADOW
	For S=Each SHADOW
		;RotateMesh S\Entity,0,SHADOW_ALIGNMENT_ANGLE,0
		;PositionEntity S\Entity,SHADOW_OFFSET,0-SHADOW_OFFSET,GENERIC_Z_OFFSET+SHADOW_Z_ALIGNMENT,False
		PointEntity S\Entity,CAMERA
	Next
End Function
;~IDEal Editor Parameters:
;~F#10#14#21#28#2C#32
;~C#Blitz3D