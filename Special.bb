Const SPECIAL_SCORE=0			;RED
Const SPECIAL_EXTEND=1			;GREEN
Const SPECIAL_SHRINK=2			;YELLOW
Const SPECIAL_FADE=3				;BLUE
Const SPECIAL_SLOW=4				;MAGENTA
Const SPECIAL_MULTIBALL=5	;CYAN
Const SPECIAL_LIFE=6				;GREY

Const SPECIAL_MAX=6

Const SPECIAL_SCORE_POINTS=500

Const SPECIAL_SHADOWSHINE_SIZE#=3.0

Const MULTIBALL_ADD=2

Global SPECIAL_MASTER[SPECIAL_MAX]

Const SPECIAL_CHANCE#=0.15
Const SPECIAL_FALL_SPEED#=0.333

Const SPECIAL_BAT_INCREASE#=1.5
Const SPECIAL_BAT_DECREASE#=0.75

Global SPECIAL_FADE_ON

Type SPECIAL
	Field Entity
	;Field Shadow.SHADOW
	Field SpecialType
	Field Colour
End Type

Function UpdateSpecials()
	Local SP.SPECIAL
	For SP=Each SPECIAL
		TurnEntity SP\Entity,0,0,6,True
		TranslateEntity SP\Entity,0,0-SPECIAL_FALL_SPEED#,0,True
		CreateShine(EntityX(SP\Entity,True),EntityY(SP\Entity,True),SPECIAL_SHADOWSHINE_SIZE,SPECIAL_SHADOWSHINE_SIZE,SP\Colour)
		
		If (EntityZ(SP\Entity)<=BOUND_DEATHZONE_Y)
			DestroySpecial(SP)
		Else
			If (EntityY(SP\Entity)>(0-0.75)) And (EntityY(SP\Entity)<0.75)
				If (GetSpecialBatCollision(EntityX(SP\Entity,True)))
					
					DoSpecialPoints(SP\SpecialType)
					
					DoPowerUp(SP\SpecialType)
					DestroySpecial(SP,True)
				End If
			End If
		End If
	Next
End Function

Function DoSpecialPoints(SpecialType)
	Local Recip=(SPECIAL_MAX*0.5)
	Local Order=Recip-SpecialType
	Local Base=Recip-Order
	
	Local Points= ( Base * 25)
	
	AddScore(Points)
End Function

Function DoPowerUp(SpecialType)
	WriteLog("Applying special type "+Str(SpecialType))
	
	Select SpecialType
		Case SPECIAL_EXTEND:
			ExtendBat
		Case SPECIAL_SHRINK:
			ShrinkBat
		Case SPECIAL_FADE:
			FadeOn
		Case SPECIAL_SLOW:
			SlowBall
		Case SPECIAL_MULTIBALL:
			Local Multi
			For Multi= 1 To MULTIBALL_ADD
				AddBallToPlay()
			Next
		Case SPECIAL_LIFE:
			If (ExtraLifeAwardValid()) Then AwardExtraLife
		Default:
			;Case SPECIAL_SCORE:
			AddScore(SPECIAL_SCORE_POINTS)	
	End Select
	
	If (SpecialType<>SPECIAL_FADE) And (SPECIAL_FADE_ON)
		;To be nice. Collecting any SPECIAL other than another FADE will cancel the existing fade.
		FadeOff
	End If
	
End Function

Function DestroySpecial(SP.SPECIAL,Notify=False)
	If (SP<>Null)
		If Notify Then WriteLog("Special type #"+SP\SpecialType+" destroyed")
		;FreeShadow(SP\Shadow)
		FreeEntity SP\Entity
		Delete SP
	End If
End Function

Function RemoveAllSpecials()
	Local SP.SPECIAL
	Local Count=0
	For SP=Each SPECIAL
		If (SP<>Null)
			DestroySpecial(SP,False)
			Count=Count+1
		End If
	Next
	If (Count)
		WriteLog("Removed "+Str(Count)+" remaining Specials")
	Else
		WriteLog("No legacy specials remained for cleaning")
	End If
End Function

Function DetermineSpecialType()
	;Adds weighting to the probability of the type of special spawned.
	Local Common=Rand(100)
	If (Common<20) Then Return SPECIAL_SCORE
	If (Common<45) Then Return SPECIAL_SHRINK
	If (Common<60) Then Return SPECIAL_FADE
	If (Common<73) Then Return SPECIAL_SLOW
	If (Common<85) Then Return SPECIAL_EXTEND
	If (Common<95) Then Return SPECIAL_MULTIBALL
	Return SPECIAL_LIFE
End Function

Function SpawnSpecial(X#,Y#)
	Local SP.SPECIAL=New SPECIAL
	SP\SpecialType=DetermineSpecialType()
	
	WriteLog("Spawning Special of type #"+SP\SpecialType+" at "+Str(X)+" , "+Str(Y))
	SP\Entity=CopyEntity(SPECIAL_MASTER[SP\SpecialType])
	EntityColor SP\Entity,GetR(SP\SpecialType+1),GetG(SP\SpecialType+1),GetB(SP\SpecialType+1)
	SP\Colour=GetLightColour((SP\SpecialType Mod COL_SELECTION_GREY) +1)
	EntityFX SP\Entity,17
	;No shadows since shadows are children and offset rather than raytraced position. Rotation of the Special entities makes the shadows move
;	SP\Shadow=CreateShadow(SPECIAL_SHADOWSHINE_SIZE,SPECIAL_SHADOWSHINE_SIZE,SP\Entity)
	PositionEntity SP\Entity,X,Y,GENERIC_Z_OFFSET,True
	ShowEntity SP\Entity
End Function

Function ChanceToSpawnSpecial()
	Return (Rnd(0.0,1.0)<SPECIAL_CHANCE)
End Function

Function PopulateSpecialMasterMesh(n)
	Local c$
	Select n
		Case SPECIAL_SCORE:
			c="P"
		Case SPECIAL_EXTEND:
			c="E"
		Case SPECIAL_FADE:
			c="F"
		Case SPECIAL_SLOW:
			c="S"
		Case SPECIAL_SHRINK:
			c="R"
		Case SPECIAL_MULTIBALL:
			c="M"
		Case SPECIAL_LIFE:
			c="X"	
			
	End Select
	
	Local Mesh=CopyMesh(ALIEN_MASTER[1]);Flat Disc style Alien
	FlipMesh Mesh
	RotateMesh Mesh,0-90,0,0
	ScaleMesh Mesh,0-1,1,1
;	PointEntity Mesh,CAMERA
	TextureSpecialEntity(Mesh,c$)
	HideEntity Mesh
	Return Mesh
End Function
	
Function TextureSpecialEntity(Entity,Char$)
	Local Image=UI_DisplayTextImage(Char)
	Local Texture=CopyCharImageToTexture(Image)
	FreeImage Image
	
	EntityTexture Entity,Texture
	FreeTexture Texture
End Function

Function ExtendBat()
	If (BAT_LENGTHSIZE=BAT_DEF_LENGTHSIZE)
		WriteLog("Bat extended from "+Str(BAT_LENGTHSIZE))
		BAT_LENGTHSIZE=BAT_DEF_LENGTHSIZE*SPECIAL_BAT_INCREASE
		ScaleEntity BATINT,SPECIAL_BAT_INCREASE,1,1,True
		
	Else
		If (BAT_LENGTHSIZE<BAT_DEF_LENGTHSIZE)
			WriteLog("Bat extended from "+Str(BAT_LENGTHSIZE))
			
			ResetBatSize
			
;			BAT_LENGTHSIZE=BAT_DEF_LENGTHSIZE
;			ScaleEntity BATINT,SPECIAL_BAT_INCREASE,1,1
			
		Else
			WriteLog("Bat not extended from "+Str(BAT_LENGTHSIZE))
			;ScaleEntity BATINT,1.0,1,1
		End If
	End If
	WriteLog("Bat length now "+Str(BAT_LENGTHSIZE));+" Actual internal Width "+Str(MeshWidth(BATINT)))
	
	EntityBox BAT,0-(BAT_LENGTHSIZE),0,0,BAT_LENGTHSIZE*2,BAT_THICKSIZE,1
	
	UpdateBat
End Function

Function ShrinkBat()
	If (BAT_LENGTHSIZE=BAT_DEF_LENGTHSIZE)
		WriteLog("Bat shrunk from "+Str(BAT_LENGTHSIZE))
		BAT_LENGTHSIZE=BAT_DEF_LENGTHSIZE*SPECIAL_BAT_DECREASE
		ScaleEntity BATINT,SPECIAL_BAT_DECREASE,1,1
		
	Else
		If (BAT_LENGTHSIZE>BAT_DEF_LENGTHSIZE)
			WriteLog("Bat shrunk from "+Str(BAT_LENGTHSIZE))
			
			ResetBatSize
;			BAT_LENGTHSIZE=BAT_DEF_LENGTHSIZE
;			ScaleEntity BATINT,SPECIAL_BAT_DECREASE,1,1
			
		Else
			WriteLog("Bat not shrunk from "+Str(BAT_LENGTHSIZE))
			;ScaleEntity BATINT,1.0,1,1
			
		End If
	End If
	WriteLog("Bat length now "+Str(BAT_LENGTHSIZE));+" Actual internal Width "+Str(MeshWidth(BATINT)))	
	
	
	EntityBox BAT,0-(BAT_LENGTHSIZE),0,0,BAT_LENGTHSIZE*2,BAT_THICKSIZE,1
	
	UpdateBat
End Function

Function FadeOn()
	If (SPECIAL_FADE_ON)
		WriteLog("Bat Fade already active, no change made.")
	Else
		SPECIAL_FADE_ON=True
		WriteLog("Bat Fade activated")
	End If
End Function

Function FadeOff()
	If (SPECIAL_FADE_ON)
		SPECIAL_FADE_ON=False
		WriteLog("Bat Fade de-activated")
		EndBatFade
	Else
		WriteLog("Bat Fade already inactive, no change made.")
	End If
End Function

Function AddBallToPlay()
	If (BALLSINPLAY<MAX_BALLS_IN_PLAY)
		
		Local Main.BALL=First BALL
		Local Speed#=Sqr( (Main\VelX*Main\VelX) + (Main\VelY*Main\VelY) )
		Local Angle=Rnd(0.0,360.0)
		Local Add.BALL=SpawnBall(Angle,Speed)
		Local X#=EntityX(Main\Entity,True)+Sin(Angle)
		Local Y#=EntityY(Main\Entity,True)+Cos(Angle)
		
		PositionEntity Add\Entity,X,Y,GENERIC_Z_OFFSET,True
		
		WriteLog("Introducing additional ball into play at "+Str(X)+" , "+Str(Y))
		
		Add\OriginalShineColour=AdditionalBallShineColour()
		Add\ShineColour=Add\OriginalShineColour
		
		;May help alleviate rare bug due to newly spawned multiballs colliding with each otehr and a brick at the same instant on spawnin...
		ResetEntity Add\Entity
	Else
		WriteLog("Unable to add more balls. Maximum reached")
	End If
End Function
;~IDEal Editor Parameters:
;~F#1A#21#38#42#61#6A#7A#86#95#99#B7#C0#DB#F7#100#10A
;~C#Blitz3D