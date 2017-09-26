Include"Patterns.bb"

Const BRICK_REGULAR=1
;Const BRICK_TOUGHENED=2
Const BRICK_INVINCIBLE=4

Const BRICKTEXTURE_FLAGS=769
Const BRICKTEXTURE_WIDTH=16
Const BRICKTEXTURE_HEIGHT=16

Const PATTERN_BRICKS_W=12
Const PATTERN_BRICKS_H=8

Const BRICK_TRANSPARENCY#=0.5
Const BRICK_FX=17
Const BRICK_SPECULAR#=1.0

Type BRICK_DATA
	Field Entity
	Field BrickType
	Field Colour
	Field X#
	Field Y#
	Field Points
End Type

Global BRICK_MASTER_REGULAR
;Global BRICK_MASTER_TOUGHENED
Global BRICK_MASTER_INVINCIBLE

Global BRICKTEXTURE_REGULAR
;Global BRICKTEXTURE_TOUGHENED
;Global BRICKTEXTURE_INVINCIBLE

Global BRICKW#
Global BRICKH#
Global BRICKD#=1.0

Global STAGE_DESTRUCTABLE_BRICKS

Function CreateBrickTextureRegular()
	Local Texture=CreateTexture(BRICKTEXTURE_WIDTH,BRICKTEXTURE_HEIGHT,BRICKTEXTURE_FLAGS)
	Local Buffer=TextureBuffer(Texture)
	Local X
	Local Y
	
	LockBuffer Buffer
	
	For Y=1 To BRICKTEXTURE_HEIGHT-2
		For X=1 To BRICKTEXTURE_WIDTH-2
			WritePixelFast X,Y,-1,Buffer
		Next
	Next
	UnlockBuffer Buffer
		
	Return Texture
End Function

Function CreateBrickTextureToughened()
	Local Texture=CreateTexture(BRICKTEXTURE_WIDTH,BRICKTEXTURE_HEIGHT,BRICKTEXTURE_FLAGS)
	Local Buffer=TextureBuffer(Texture)
	CopyRect 0,0,BRICKTEXTURE_WIDTH,BRICKTEXTURE_HEIGHT,0,0,TextureBuffer(BRICKTEXTURE_REGULAR),Buffer
	
	Local X
	Local Y
	
	LockBuffer Buffer
	
	For Y=BRICKTEXTURE_HEIGHT-(BRICKTEXTURE_HEIGHT*0.5) To BRICKTEXTURE_HEIGHT-2
		For X=1 To BRICKTEXTURE_WIDTH-2
			WritePixelFast X,Y,GREYTONE,Buffer
			WritePixelFast Y,X,GREYTONE,Buffer
		Next
	Next
	UnlockBuffer Buffer
	
	Return Texture
End Function

Function CreateBrickTextureInvincible()
	Local Texture=CreateTexture(BRICKTEXTURE_WIDTH,BRICKTEXTURE_HEIGHT,BRICKTEXTURE_FLAGS)
	Local Buffer=TextureBuffer(Texture)
	CopyRect 0,0,BRICKTEXTURE_WIDTH,BRICKTEXTURE_HEIGHT,0,0,TextureBuffer(BRICKTEXTURE_REGULAR),Buffer
	
	Local X
	Local Y
	
	LockBuffer Buffer
	
	For Y=1 To BRICKTEXTURE_HEIGHT-2
		WritePixelFast Y,Y,0,Buffer
		WritePixelFast (BRICKTEXTURE_HEIGHT-1)-Y,Y,0,Buffer
	Next
	UnlockBuffer Buffer
	Return Texture
End Function

Function CreateBrick(BrickType)
	Local Brick
	
	Select BrickType
;		Case BRICK_TOUGHENED:
;			Brick=CopyEntity(BRICK_MASTER_TOUGHENED)
		Case BRICK_INVINCIBLE:
			Brick=CopyEntity(BRICK_MASTER_INVINCIBLE)
		Default :;(BRICK_REGULAR)
			Brick=CopyEntity(BRICK_MASTER_REGULAR)
			EntityFX Brick,BRICK_FX
			EntityAlpha Brick,BRICK_TRANSPARENCY
	End Select
	
	EntityShininess Brick,BRICK_SPECULAR
	
	Return Brick
	
End Function			

Function PlaceBrick(X,Y,DataByte)
	Local Colour=(DataByte And (COL_SELECTION_RED+COL_SELECTION_GREEN+COL_SELECTION_BLUE))
	
	If (Colour)
		
		Local R
		Local G
		Local B
		
		R=GetR(Colour)
		G=GetG(Colour)
		B=GetB(Colour)
		
		Local BrickType=BRICK_REGULAR
;		If (DataByte And 8)
;			BrickType=BRICK_TOUGHENED
			
;		End If
		
		Local PAT.BRICK_DATA=New BRICK_DATA
		
		If (Colour=COL_SELECTION_GREY)
			BrickType=BRICK_INVINCIBLE
		Else
			STAGE_DESTRUCTABLE_BRICKS=STAGE_DESTRUCTABLE_BRICKS+1
		End If
		
		PAT\Colour=Colour
		PAT\BrickType=BrickType
		
		Local Brick=CreateBrick(BrickType)
		
		PAT\Entity=Brick
		
		NameEntity Brick,Str(Handle(PAT))
		
		EntityColor Brick,R,G,B	
		
		PAT\Points=DetermineBrickPoints(Colour,BrickType)
		
		EntityType Brick,COLLISION_BRICK
		
		
		; EITHER WORKS BUT RESULTS IN TUNNELLING IN RARAE CIRCUMSTANCE...
		EntityRadius Brick,BRICKW,BRICKH
	;	EntityBox Brick,0-BRICKW,0-BRICKH,0-BRICKD,BRICKW*2,BRICKH*1.5,BRICKD*2
		
		
		PAT\X#=ConvertX(X)
		PAT\Y#=ConvertY(Y)
		
		PositionEntity Brick,PAT\X,PAT\Y,GENERIC_Z_OFFSET,True
		
	End If
		
End Function

Function ConvertX#(GridX)
	Return ((GridX*BRICKW*2)-(PATTERN_BRICKS_W*BRICKW))+(BRICKW)
End Function

Function ConvertY#(GridY)
	Return (GridY*BRICKH*2)+BOUNDARY_BRICK_START_Y
End Function

Function RemoveAllBricks()
	WriteLog("Removing any remaining/existing bricks")
	Local BR.BRICK_DATA
	For BR=Each BRICK_DATA
		DestroyBrick(BR)
	Next
	
	STAGE_DESTRUCTABLE_BRICKS=0
End Function

Function DetermineBrickPoints(Colour,BrickType)
	Local Bonus=50*BrickType
	Return Colour*Bonus
End Function

Function AttemptToDestroyBrick(BR.BRICK_DATA)
	If (BR\BrickType<>BRICK_INVINCIBLE)
;		If (BR\BrickType=BRICK_TOUGHENED)
;			DemoteBrick(BR)
;		Else
			AddScore(BR\Points)
			If (ChanceToSpawnSpecial()) Then SpawnSpecial(BR\X,BR\Y)
			
			PlayBrickSound(SND_BRICK_DESTROY)
			
			
			ExplodeBrick(BR)
			DestroyBrick(BR)
			STAGE_DESTRUCTABLE_BRICKS=STAGE_DESTRUCTABLE_BRICKS-1
			If (STAGE_DESTRUCTABLE_BRICKS<1)
				WriteLog("All bricks destroyed")
				NextRound
			End If
;		End If
			
		Else		
			PlayBrickSound(SND_BRICK_INVINCIBLE)
		End If
		
		
End Function

Function ExplodeBrick(BR.BRICK_DATA)
	Local R=GetR(BR\Colour)
	Local G=GetG(BR\Colour)
	Local B=GetB(BR\Colour)
	
	Local X#=BR\X
	Local Y#=BR\Y
	
	CreateExplosion(X,Y,R,G,B,True)
End Function	

Function UpdateBricks()
	If (CheckForImpossibleBrickTimeout())
		RemoveImpossibleBricks
	End If
End Function

Function DemoteBrick(BR.BRICK_DATA)
	Local X#=BR\X
	Local Y#=BR\Y
	Local C=BR\Colour
	Local Points=BR\Points
	DestroyBrick(BR)
	
	BR.BRICK_DATA=New BRICK_DATA
	BR\BrickType=BRICK_REGULAR
	BR\Colour=C
	BR\X=X
	BR\Y=Y
	BR\Points=Points
	BR\Entity=CreateBrick(BRICK_REGULAR)
	
	Local R=GetR(C)
	Local G=GetG(C)
	Local B=GetB(C)
	EntityType BR\Entity,COLLISION_BRICK
	ResetEntity BR\Entity
	
	EntityColor BR\Entity,R,G,B	
	
	NameEntity BR\Entity,Str(Handle(BR))
End Function

Function DestroyBrick(BR.BRICK_DATA)
	If (BR\Entity)
		ResetEntity BR\Entity
		FreeEntity BR\Entity
	End If
	Delete BR
End Function
;~IDEal Editor Parameters:
;~F#11#28#3A#4F#61#75#AE#B2#C0#C5#E0#EB#F1#10B
;~C#Blitz3D