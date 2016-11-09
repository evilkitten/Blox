Const CREDITS_DISPLAY_DURATION=2000

Type CREDITS
	Field AccreditationLine$
End Type

Function PopulateCredits()
	Local C.CREDITS
	
	C=New CREDITS
	C\AccreditationLine="BLOX"
	
	C=New CREDITS
	C\AccreditationLine="BY"
	
	C=New CREDITS
	C\AccreditationLine="PJCHOWDHURY"
	
	C=New CREDITS
	C\AccreditationLine="SCORE"
	
	C=New CREDITS
	C\AccreditationLine="BENJAMINTISSOT"
	
	C=New CREDITS
	C\AccreditationLine="QATESTERS"
	
	C=New CREDITS
	C\AccreditationLine="JAMESREEVES"
	
	C=New CREDITS
	C\AccreditationLine="THERESEWIHLNEY"
	
End Function

Function ScrollCredits()
	FlushKeys
	FlushJoy
	
	Local MaintainCredits=True
	Local X=GWIDTH*0.5
	Local Y=GHEIGHT*0.5
	Local Image
	Local C.Credits=First Credits
	
	Local TimeDifference
	Local CreditsDisplayTimestamp=MilliSecs()
	
	Image=UI_DisplayTextImage(C\AccreditationLine)
	HandleImage Image,ImageWidth(Image)*0.5,0
	
	While MaintainCredits
		RenderWorld
		
		TimeDifference= MilliSecs()-CreditsDisplayTimestamp
		If  (TimeDifference>CREDITS_DISPLAY_DURATION)
			C=After C
			
			If (Image) 
				FreeImage Image
			End If
			
			If (C=Null) 
				MaintainCredits=False
				Exit
			End If
			
			Image=UI_DisplayTextImage(C\AccreditationLine)
			MidHandle Image
			
			CreditsDisplayTimestamp=MilliSecs()-(TimeDifference-CREDITS_DISPLAY_DURATION)
		End If
		
		DrawImage Image,X,Y
		
		Flip True
		
		If( (KeyHit(CTRL_KEY_LAUNCH))Or(JoyHit(CTRL_JOY_LAUNCH,JOY_PORT)))
			MaintainCredits=False
			Exit
		End If
		
	Wend
	
	If (Image)
		FreeImage Image
	End If
	
	If (STATE<>STATE_ATTRACT)
		ChangeToAttractMode
	End If
End Function
;~IDEal Editor Parameters:
;~F#2#6#23
;~C#Blitz3D