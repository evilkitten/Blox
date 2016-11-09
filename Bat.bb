
Const BAT_DEF_LENGTHSIZE#=4.0

Const BAT_THICKSIZE#=1.0
Const BAT_INNERTHICKSIZE#=0.9
Const BAT_SPECULAR_RATIO#=0.85
Const BATMID_FX=17
Const BATINT_FX=57

Const BAT_MID_TRANSPARENCY_RATIO#=0.333
Const BAT_TRANSPARENCYBLENDING=3

Const BAT_SHINE_TRANSPARENCY#=0.85

Const BATINTERNALETEXTURE_WIDTH=4
Const BATINTERNALETEXTURE_HEIGHT=4
Const BATINTERNALETEXTUREFLAGS=783

Const BAT_COLOR_R=128
Const BAT_COLOR_G=160
Const BAT_COLOR_B=224

Const BAT_INTERNALREVOLUTION_SPEED#=1.0
;Global BAT_SHINE_COLOUR

Const BATSPEED_MAX#=2.5
Const BAT_SPEED_INCREMENT#=0.25
Const BAT_SPEED_DAMP#=0.1;This is not absolute, but ratio of BAT_SPEED_INCREMENT


Const BAT_COLLISIONBALL_INFLUENCE#=0.01

Global BAT
Global BATENDL
Global BATENDR
Global BATMID
Global BATINT

Global BATSPEED#

Global BAT_ANIM_COLR
Global BAT_ANIM_COLG
Global BAT_ANIM_COLB

Global BAT_ANIM_COLRd
Global BAT_ANIM_COLGd
Global BAT_ANIM_COLBd

Global BAT_ANIM_COLNEXT

Global BAT_LENGTHSIZE#

Function BatMotion()
	TranslateEntity BAT,BATSPEED,0,0,True
	BatShine()
End Function

Function BatShine()
	Local Shiner
	If BATSPEED>0 Then Shiner= BATENDL
	If BATSPEED<0 Then Shiner= BATENDR
	
	If (Shiner)
		;CreateShine(EntityX(Shiner,True),0,BAT_THICKSIZE*2*BATSPEED,BAT_THICKSIZE*BATSPEED,BAT_SHINE_COLOUR,BAT_SHINE_TRANSPARENCY)
		CreateShine(EntityX(Shiner,True),0,BAT_THICKSIZE*2*BATSPEED,BAT_THICKSIZE*BATSPEED, BAT_ANIM_COLR+(BAT_ANIM_COLG Shl 8)+(BAT_ANIM_COLB Shl 16),BAT_SHINE_TRANSPARENCY)
	End If
End Function

Function UpdateBat()
;	BatBoundary
	
	PositionEntity BAT,EntityX#(BAT,True),0,GENERIC_Z_OFFSET,True
	
	If (Abs(EntityX(BATENDL,False)-EntityX(BATENDR,False)))<>(BAT_LENGTHSIZE*2)
		;Keep bat together when different parts collide separately
		
		If (EntityX(BAT,True)<0)
			;Left side
			PositionEntity BATENDR,EntityX(BATENDL,True)+(BAT_LENGTHSIZE*2),0,GENERIC_Z_OFFSET,True
			PositionEntity BAT,EntityX(BATENDL,True)+(BAT_LENGTHSIZE),0,GENERIC_Z_OFFSET,True
		End If
		
		If (EntityX(BAT,True)>0)
			;Right side
			PositionEntity BATENDL,EntityX(BATENDR,True)-(BAT_LENGTHSIZE*2),0,GENERIC_Z_OFFSET,True
			PositionEntity BAT,EntityX(BATENDR,True)-(BAT_LENGTHSIZE),0,GENERIC_Z_OFFSET,True
		End If
		
	Else
		
		;Otherwise maintain standard proportions
		PositionEntity BATENDL,0-BAT_LENGTHSIZE,0,GENERIC_Z_OFFSET,0
		PositionEntity BATENDR,BAT_LENGTHSIZE,0,GENERIC_Z_OFFSET,0
		
	End If
	
	BatBoundary
	
	PositionEntity BATINT,EntityX(BAT,True),0,GENERIC_Z_OFFSET,True
	PositionEntity BATMID,EntityX(BAT,True),0,GENERIC_Z_OFFSET,True
End Function

Function UpdateBatFade()
	Local Fade#=EntityPitch(BATINT)/180.0
	EntityAlpha BATMID,BAT_MID_TRANSPARENCY_RATIO*Fade
	EntityAlpha BATENDL,Fade
	EntityAlpha BATENDR,Fade
	EntityAlpha BATINT,Fade
End Function

Function EndBatFade()
	EntityAlpha BATMID,BAT_MID_TRANSPARENCY_RATIO
	EntityAlpha BATENDL,1.0
	EntityAlpha BATENDR,1.0
	EntityAlpha BATINT,1.0
End Function

Function UpdateBatInt()
	TurnEntity BATINT,BAT_INTERNALREVOLUTION_SPEED,0,0,True
	If (SPECIAL_FADE_ON)
		UpdateBatFade
	Else
		UpdateBatColour
	End If
End Function

Function UpdateBatColour()
	If ((BAT_ANIM_COLR=GetR(BAT_ANIM_COLNEXT)) And (BAT_ANIM_COLG=GetG(BAT_ANIM_COLNEXT)) And (BAT_ANIM_COLB=GetB(BAT_ANIM_COLNEXT)))
		UpdateBatColourLimits		
	End If	
	BAT_ANIM_COLR=BAT_ANIM_COLR+BAT_ANIM_COLRd
	BAT_ANIM_COLG=BAT_ANIM_COLG+BAT_ANIM_COLGd
	BAT_ANIM_COLB=BAT_ANIM_COLB+BAT_ANIM_COLBd
	
	EntityColor BATINT,BAT_ANIM_COLR,BAT_ANIM_COLG,BAT_ANIM_COLB
End Function

Function UpdateBatColourLimits()
	BAT_ANIM_COLNEXT=(BAT_ANIM_COLNEXT+1) Mod COLOURS_SELECTION
	If (BAT_ANIM_COLNEXT=0) Then BAT_ANIM_COLNEXT=1
	
	Local R=GetR(BAT_ANIM_COLNEXT)
	Local G=GetG(BAT_ANIM_COLNEXT)
	Local B=GetB(BAT_ANIM_COLNEXT)
	
	BAT_ANIM_COLRd=Sgn(R-BAT_ANIM_COLR)
	BAT_ANIM_COLGd=Sgn(G-BAT_ANIM_COLG)
	BAT_ANIM_COLBd=Sgn(B-BAT_ANIM_COLB)
End Function

Function ResetBatSize()
	BAT_LENGTHSIZE=BAT_DEF_LENGTHSIZE
	ScaleEntity BATINT,1.0,1,1
	EntityBox BAT,0-(BAT_LENGTHSIZE),0,0,BAT_LENGTHSIZE*2,BAT_THICKSIZE,1
	UpdateBat
	
	WriteLog("Bat Size Reset. Bat length now "+Str(BAT_LENGTHSIZE));+" Actual internal Width "+Str(MeshWidth(BATINT)))
End Function
;~IDEal Editor Parameters:
;~F#34#39#44#66#6E#75#7E#89#96
;~C#Blitz3D