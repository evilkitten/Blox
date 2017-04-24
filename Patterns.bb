Const PATTERN_INVINCIBLE_RATIO_LIMIT#=0.25

Const KNOWN_ROUNDS=4

Const PATTERN_STYLES=7

Const PATTERN_STYLE_LINEH=1
Const PATTERN_STYLE_LINEV=2
Const PATTERN_STYLE_CROSS=4
Const PATTERN_STYLE_CORNER=8
Const PATTERN_STYLE_BOX=16
Const PATTERN_STYLE_CENTRE=32
Const PATTERN_STYLE_X=64

Const PATTERN_MIX=4

Const PATTERN_AUDIT_PERCENTAGE=63; Ratio of bricks to empty

Const PATTERN_COLOR_MODES=3
Const PATTERN_COLOR_MODE_NULL=0
Const PATTERN_COLOR_MODE_RAINBOW=1
Const PATTERN_COLOR_MODE_STRIPED=2
Const PATTERN_COLOR_MODE_SINGLE=3

Global IMPOSSIBLE_BRICKS_REMOVED;Tracks per round whether the time limited removal of impossible bricks has occurred.

Dim TempBrickArray(0,0)
Dim RepeatArray(0,0)

Function LoadPattern(n)
	
	;Deprecated: Patterns procedurally generated.
	
;	Local FilePath$="Stage\Stage"+Str(n)+".dat"
;	If (FileType(FilePath)<>1)
		
;		n=(n Mod KNOWN_ROUNDS)+1
		
;		FilePath$="Stage\Stage"+Str(n)+".dat"
		
;		If (FileType(FilePath<>1))
;			RuntimeError "Strage Data Undefined"
;		End If
;	End If
	
;	WriteLog("Loading Pattern from "+FilePath)
	
;	Local File=ReadFile(FilePath)
	
;	Local X
;	Local Y
	
;	Local BrickByte
;	Local Bricks
;	For Y=0 To PATTERN_BRICKS_H-1
;		For X=0 To PATTERN_BRICKS_W-1 Step 2
;			BrickByte=ReadByte(File)
;			For Bricks =0 To 1
;				If (Bricks=1) Then BrickByte=(BrickByte Shr 4)
;				PlaceBrick(X+Bricks,Y,BrickByte And COL_SELECTION_GREY)
;			Next
;		Next
;	Next
	
;	CloseFile File
	
End Function

Function SetRoundPattern()
	;Reset Pattern Data
	InitialisePatterns
	
	SelectBrickPattern
	
End Function

Function SetRandomBrickPattern()
	CreateTempPattern
	RepeatPattern
	AuditPattern
	SetPatternArrayToBricks
End Function

Function SetPatternArrayToBricks()
	Local X
	Local Y
	
	For Y=0 To PATTERN_BRICKS_H-1
		For X=0 To PATTERN_BRICKS_W-1
			PlaceBrick(X,Y,TempBrickArray(X,Y))
		Next
	Next
	Dim TempBrickArray(0,0)
End Function

Function GetRandomPatternStyle()
	Local Mix=Rand(1,PATTERN_MIX)
	Local Max=PATTERN_STYLES-1
	Local Pattern
	Local Addition
	
	While Pattern=0
		
		Local Iter
		
		For Iter= 1 To Mix
			Addition=1 Shl (Rand(0,Max))
			If (Pattern And Addition)
				
			Else
				
			End If
			Pattern=(Pattern Xor Addition)
		Next
		
	Wend
	
	Return Pattern
End Function

Function CreateTempPattern(Override=0)
	Dim TempBrickArray(PATTERN_BRICKS_W-1,PATTERN_BRICKS_H-1)
	
	Local Pattern=GetRandomPatternStyle()
	If (Override) Then Pattern=Override
	
	WriteLog("Generating Brick Pattern with initial parameter "+Str(Pattern))
	
	Local Bit
	Local Iter
	
	For Iter=1 To  PATTERN_STYLES
		Bit=1 Shl (Iter-1)
		
		If (Pattern And Bit)
			AddPattern(Bit)
		End If
	Next
End Function

Function AddPattern(Pattern)
	Select Pattern
		Case PATTERN_STYLE_LINEH:
			PatternLineH(True)
		Case PATTERN_STYLE_LINEV:
			PatternLineV(True)
		Case PATTERN_STYLE_CROSS:
			PatternCross(True)
		Case PATTERN_STYLE_CORNER:
			PatternCorner(True)
		Case PATTERN_STYLE_BOX:
			PatternBox(True)
		Case PATTERN_STYLE_CENTRE:
			PatternCentre(True)
		Case PATTERN_STYLE_X:
			PatternX(True)
	End Select
End Function

Function RemovePattern(Pattern)
	Select Pattern
		Case PATTERN_STYLE_LINEH:
			PatternLineH(False)
		Case PATTERN_STYLE_LINEV:
			PatternLineV(False)
		Case PATTERN_STYLE_CROSS:
			PatternCross(False)
		Case PATTERN_STYLE_CORNER:
			PatternCorner(False)
		Case PATTERN_STYLE_BOX:
			PatternBox(False)
		Case PATTERN_STYLE_CENTRE:
			PatternCentre(False)
		Case PATTERN_STYLE_X:
			PatternX(False)
	End Select
End Function

Function ChooseRandomPattern()
	Return 1 Shl Rand(0,PATTERN_STYLES-1)
End Function

Function AuditPattern()
	
	WriteLog("Auditing Procedural brick pattern")
	
	Local CountR
	Local CountG
	Local CountB
	Local CountC
	Local CountM
	Local CountY
	Local CountTotal
	
	Local W=PATTERN_BRICKS_W
	Local H=PATTERN_BRICKS_H
	
	Local Possible=W*H
	Local Ratio#
	
	Local X
	Local Y
	
	Local Colour
	
	For Y=0 To H-1
		For X=0 To W-1
			Colour=TempBrickArray(X,Y)
			Select(Colour)
				Case COL_SELECTION_RED:
					CountR=CountR+1
					
				Case COL_SELECTION_GREEN:
					CountG=CountG+1	
					
				Case COL_SELECTION_BLUE:
					CountB=CountB+1
					
				Case COL_SELECTION_YELLOW:
					CountY=CountY+1
					
				Case COL_SELECTION_CYAN:
					CountC=CountC+1
					
				Case COL_SELECTION_MAGENTA:
					CountM=CountM+1
					
			End Select
		Next
	Next
	
	CountTotal=CountR+CountG+CountB+CountY+CountC+CountM
	Ratio#=(Float(CountTotal)/Float(Possible))
	Ratio=Ratio*100.0
	
	If (Ratio<(100-PATTERN_AUDIT_PERCENTAGE))
		AddPattern(ChooseRandomPattern())
	End If
	
	If (Ratio>PATTERN_AUDIT_PERCENTAGE)
		RemovePattern(ChooseRandomPattern())
	End If	
	
	CountR=0
	CountG=0
	CountB=0
	CountY=0
	CountC=0
	CountM=0
	
	X=0
	Y=0
	
	For Y=0 To H-1
		For X=0 To W-1
			Colour=TempBrickArray(X,Y)
			Select(Colour)
				Case COL_SELECTION_RED:
					CountR=CountR+1
					
				Case COL_SELECTION_GREEN:
					CountG=CountG+1	
					
				Case COL_SELECTION_BLUE:
					CountB=CountB+1
					
				Case COL_SELECTION_YELLOW:
					CountY=CountY+1
					
				Case COL_SELECTION_CYAN:
					CountC=CountC+1
					
				Case COL_SELECTION_MAGENTA:
					CountM=CountM+1
					
			End Select
		Next
	Next
	
	Local MultiColour
	If (CountR)
		MultiColour=(CountG+CountB+CountY+CountC+CountM)
	Else
		If (CountG)
			MultiColour=(CountB+CountY+CountC+CountM)
		Else
			If (CountB)
				MultiColour=(CountY+CountC+CountM)
			Else
				If (CountY)
					MultiColour=(CountC+CountM)
				Else
					If (CountC)
						MultiColour=CountM
					End If
				End If
			End If
		End If
	End If
	
	If (MultiColour)
		Local InvColour=ChooseColourFromAvailable(CountR,CountG,CountB,CountY,CountC,CountM)
		If (InvColour)
			InvincibleColour(InvColour)
			SpecialCaseAuditInvincibleImpossibility
		End If
	End If
End Function

Function ChooseColourFromAvailable(cR,cG,cB,cY,cC,cM)
	Local Choice=0
	Local AttemptString$
	Local Sanity=0
	
	Local Max=((cR+cG+cB+cY+cC+cM)*PATTERN_INVINCIBLE_RATIO_LIMIT)
	
	Local Iter
	For Iter=COL_SELECTION_RED To COLOURS_SELECTION
		AttemptString=AttemptString+Str(Iter)
	Next	
	
	While (Choice=0)
		Sanity=Sanity+1
		
		If (Sanity>COLOURS_SELECTION)
			Return 0
		End If
		
		Local Picked=Rand(1,Len(AttemptString))
		Choice=Int(Mid(AttemptString,Picked,1))
		
		Select (Choice)
			Case COL_SELECTION_RED:
				If (Not(cR))
					Choice=0
					AttemptString=Replace(AttemptString,Str(COL_SELECTION_RED),"")
				Else
					
					If (cR>Max)
						Choice=0
						AttemptString=Replace(AttemptString,Str(COL_SELECTION_RED),"")
					End If
				End If
				
			Case COL_SELECTION_GREEN:
				If (Not(cG))
					Choice=0
					AttemptString=Replace(AttemptString,Str(COL_SELECTION_GREEN),"")
				Else
					If (cG>Max)
						Choice=0
						AttemptString=Replace(AttemptString,Str(COL_SELECTION_GREEN),"")
					End If
				End If
				
			Case COL_SELECTION_BLUE:
				If (Not(cB))
					Choice=0
					AttemptString=Replace(AttemptString,Str(COL_SELECTION_BLUE),"")
				Else
					If (cB>Max)
						Choice=0
						AttemptString=Replace(AttemptString,Str(COL_SELECTION_BLUE),"")
					End If
				End If
				
			Case COL_SELECTION_YELLOW:
				If (Not(cY))
					Choice=0
					AttemptString=Replace(AttemptString,Str(COL_SELECTION_YELLOW),"")
				Else
					If (cY>Max)
						Choice=0
						AttemptString=Replace(AttemptString,Str(COL_SELECTION_YELLOW),"")
					End If
				End If
				
			Case COL_SELECTION_CYAN:
				If (Not(cC))
					Choice=0
					AttemptString=Replace(AttemptString,Str(COL_SELECTION_CYAN),"")
				Else
					If (cC>Max)
						Choice=0
						AttemptString=Replace(AttemptString,Str(COL_SELECTION_CYAN),"")
					End If
				End If
				
			Case COL_SELECTION_MAGENTA:
				If (Not(cM))
					Choice=0
					AttemptString=Replace(AttemptString,Str(COL_SELECTION_MAGENTA),"")
				Else
					If (cM>Max)
						Choice=0
						AttemptString=Replace(AttemptString,Str(COL_SELECTION_MAGENTA),"")
					End If
				End If
				
			Default:
				;This shouldn't occur, but just in case, lets catch it and try again
				Choice=0		
		End Select
		
	Wend
	
	Return Choice
End Function

Function InvincibleColour(Colour)
	Local X
	Local Y
	
	Local Count
	
	For Y=1 To PATTERN_BRICKS_H-1
		For X=0 To PATTERN_BRICKS_W-1
			
			If (TempBrickArray(X,Y)=Colour)
				
				If( (X=Int((PATTERN_BRICKS_W*0.5))) Or (X=(Int((PATTERN_BRICKS_W-1)*0.5))) )
					;Leave this brick alone, so there's space in the centre
				Else
					TempBrickArray(X,Y)=COL_SELECTION_GREY
					Count=Count+1
				End If
				
			End If
			
		Next
	Next
	
	If (Count)
		WriteLog("Changing "+Str(Count)+" bricks of colour #"+Str(Colour)+" into invincible bricks.")
	End If
	
End Function

Function PatternLineH(AddColour=True)
	Local ColourSpread=Rand(PATTERN_COLOR_MODE_RAINBOW,PATTERN_COLOR_MODES)*AddColour
	Local Colour=Rand(COL_SELECTION_RED,COLOURS_SELECTION)*AddColour
	
	Local Lines=Rand(1,(PATTERN_BRICKS_H-1)*0.75)
	Local Split#=Float(1.0/Float(Lines))
	If (Split<1) Then Split=1
	Local Thick=Rand(1,Split)
	If Lines<1 Then Lines=1
	
	If (Not(AddColour)) 
		Thick =1
		Lines=1
	End If
	
	
	Local Y
	Local YPos
	
	For YPos=1 To Lines
		Y=(YPos*Split)-1
		PatternHStrip(Y,Thick,ColourSpread,Colour)
		PatternHStrip((PATTERN_BRICKS_H-1)-Y,Thick,ColourSpread,Colour)
	Next
	
End Function

Function PatternLineV(AddColour=True)
	Local ColourSpread=Rand(PATTERN_COLOR_MODE_RAINBOW,PATTERN_COLOR_MODES)*AddColour
	Local Colour=Rand(COL_SELECTION_RED,COLOURS_SELECTION)*AddColour
	
	Local Lines=Rand(1,(PATTERN_BRICKS_W-1)*0.75)
	Local Split#=Float(1.0/Float(Lines))
	If (Split<1) Then Split=1
	Local Thick=Rand(1,Split)
	If Lines<1 Then Lines=1
	
	If (Not(AddColour)) 
		Thick =1
		Lines=1
	End If
	
	Local X
	Local XPos
	
	For XPos=1 To Lines
		X=(XPos*Split)-1
		PatternVStrip(X,Thick,ColourSpread,Colour)
		PatternVStrip((PATTERN_BRICKS_W-1)-X,Thick,ColourSpread,Colour)
	Next
	
	
End Function

Function PatternHStrip(Yo,Thick,ColourMode=3, DefColour=0)
	Local X
	Local Y
	Local Colour=DefColour
	If (ColourMode=PATTERN_COLOR_MODE_NULL) Then Colour=0
	
	For X=0 To PATTERN_BRICKS_W-1
		If (ColourMode=PATTERN_COLOR_MODE_STRIPED) Then Colour=((Colour+1) Mod COLOURS_SELECTION)+1
		For Y=(0-(Thick*0.5)) To (Thick*0.5)
			If (Y<PATTERN_BRICKS_H)
				If (ColourMode=PATTERN_COLOR_MODE_RAINBOW) Then Colour=((Colour+1) Mod COLOURS_SELECTION)+1
				SetTempBrick(X,Yo+Y,Colour)
			Else
				Exit
			End If
		Next
	Next
End Function

Function PatternVStrip(Xo,Thick,ColourMode=3, DefColour=0)
	Local X
	Local Y
	Local Colour=DefColour
	If (ColourMode=PATTERN_COLOR_MODE_NULL) Then Colour=0
	
	For Y=0 To PATTERN_BRICKS_H-1
		If (ColourMode=PATTERN_COLOR_MODE_STRIPED) Then Colour=((Colour+1) Mod COLOURS_SELECTION)+1
		For X=(0-(Thick*0.5)) To (Thick*0.5)
			If (X<PATTERN_BRICKS_W)
				If (ColourMode=PATTERN_COLOR_MODE_RAINBOW) Then Colour=((Colour+1) Mod COLOURS_SELECTION)+1
				SetTempBrick(Xo+X,Y,Colour)
			Else
				Exit
			End If
		Next
	Next
End Function

Function PatternCross(AddColour=False)
	Local ColourSpread=Rand(PATTERN_COLOR_MODE_RAINBOW,PATTERN_COLOR_MODES)*AddColour
	Local Colour=Rand(COL_SELECTION_RED,COLOURS_SELECTION)*AddColour
	
	Local Thick=Rand(1,3)
	If (Not(AddColour)) Then Thick =1
	Local X=(PATTERN_BRICKS_W-1)*0.5
	Local Y=(PATTERN_BRICKS_H-1)*0.5
	
	PatternVStrip(X,Thick,ColourSpread,Colour)
	PatternVStrip((PATTERN_BRICKS_W-1)-X,Thick,ColourSpread,Colour)
	PatternHStrip(Y,Thick,ColourSpread,Colour)
	PatternHStrip((PATTERN_BRICKS_H-1)-Y,Thick,ColourSpread,Colour)
	
End Function

Function PatternCorner(AddColour=False)
	Local ColourSpread=Rand(PATTERN_COLOR_MODE_RAINBOW,PATTERN_COLOR_MODES)*AddColour
	Local Colour=Rand(COL_SELECTION_RED,COLOURS_SELECTION)*AddColour
	
	Local Thick=Rand(0,3)
	If (Not(AddColour)) Then Thick =1
	
	Local Y= 0
	Local X= 0
	Local LimitX=PATTERN_BRICKS_W-1
	Local LimitY=PATTERN_BRICKS_H-1
	
	For Y=0 To Thick
		If (ColourSpread=PATTERN_COLOR_MODE_RAINBOW) Then Colour=((Y+1) Mod COLOURS_SELECTION)+1
		
		For X= 0 To Thick
			If (ColourSpread=PATTERN_COLOR_MODE_STRIPED) Then Colour=(X Mod COLOURS_SELECTION)+1
			
			SetTempBrick(X,Y,Colour*AddColour)
			SetTempBrick(LimitX-X,Y,Colour*AddColour)
			SetTempBrick(X,LimitY-Y,Colour*AddColour)
			SetTempBrick(LimitX-X,LimitY-Y,Colour*AddColour)
			
		Next
	Next
	
End Function

Function PatternBox(AddColour=False)
	Local ColourSpread=Rand(PATTERN_COLOR_MODE_RAINBOW,PATTERN_COLOR_MODES)*AddColour
	Local Colour=Rand(COL_SELECTION_RED,COLOURS_SELECTION)*AddColour
	
	Local CentreX=Floor(PATTERN_BRICKS_W*0.5)
	Local CentreY=Floor(PATTERN_BRICKS_H*0.5)
	
	Local StartX
	Local StartY
	Local LimitX
	Local LimitY
	Local Size=(Rand(1,2))
	If (Not(AddColour)) Then Size=1
	
	Select Size
		Case 1:
			;Small
			StartX=2
			LimitX=PATTERN_BRICKS_W-3
			
			StartY=2
			LimitY=PATTERN_BRICKS_H-3
			
		Case 2:
			;Large
			StartX=0
			LimitX=PATTERN_BRICKS_W-1
			
			StartY=0
			LimitY=PATTERN_BRICKS_H-1
	End Select		
	
	Local Y
	
	If (ColourSpread=PATTERN_COLOR_MODE_RAINBOW) Then Colour=Rand(1,COLOURS_SELECTION)
	
	For Y=StartY To LimitY
		If (ColourSpread=PATTERN_COLOR_MODE_STRIPED) Then Colour=(Y Mod (COLOURS_SELECTION-1))+1
		SetTempBrick(StartX,Y,Colour*AddColour)
		SetTempBrick(LimitX,Y,Colour*AddColour)
	Next
	
	Local X
	If (ColourSpread=PATTERN_COLOR_MODE_RAINBOW) Then Colour=Rand(1,COLOURS_SELECTION)
	
	For X=StartX To LimitX
		If (ColourSpread=PATTERN_COLOR_MODE_STRIPED) Then Colour=(X Mod (COLOURS_SELECTION-1))+1
		SetTempBrick(X,StartY,Colour*AddColour)
		SetTempBrick(X,LimitY,Colour*AddColour)
	Next
End Function	

Function PatternCentre(AddColour=False)
	Local ColourSpread=Rand(PATTERN_COLOR_MODE_RAINBOW,PATTERN_COLOR_MODES)*AddColour
	Local Colour=Rand(COL_SELECTION_RED,COLOURS_SELECTION)*AddColour
	
	Local Size=Rand(1,3)
	
	Local CentreX=Floor(PATTERN_BRICKS_W*0.5)
	Local CentreY=Floor(PATTERN_BRICKS_H*0.5)
	
	Local StartX=CentreX-Size
	Local StartY=CentreY-Size
	Local LimitX=CentreX+Size
	Local LimitY=CentreY+Size
	
	Local X
	Local Y
	
	For Y=StartY To LimitY
		If (ColourSpread=PATTERN_COLOR_MODE_RAINBOW) Then Colour=Rand(1,COLOURS_SELECTION)
		
		For X=StartX To LimitX
			If (ColourSpread=PATTERN_COLOR_MODE_STRIPED) Then Colour=((X+Y) Mod (COLOURS_SELECTION-1))+1
			SetTempBrick(X,Y,Colour*AddColour)
		Next
		
	Next
	
End Function	

Function PatternX(AddColour=False)
	Local ColourSpread=Rand(PATTERN_COLOR_MODE_RAINBOW,PATTERN_COLOR_MODES)*AddColour
	Local Colour=Rand(COL_SELECTION_RED,COLOURS_SELECTION)*AddColour
	
	Local Offset=(PATTERN_BRICKS_W-PATTERN_BRICKS_H)*0.5
	Local Iter
	
	For Iter=0 To PATTERN_BRICKS_H-1
		If (ColourSpread=PATTERN_COLOR_MODE_STRIPED) Then Colour=(Iter Mod (COLOURS_SELECTION-1))+1
		SetTempBrick(Offset+Iter,Iter,Colour)
	Next
	If (ColourSpread=PATTERN_COLOR_MODE_RAINBOW) Then Colour=Rand(1,COLOURS_SELECTION)
	For Iter=0 To PATTERN_BRICKS_H-1
		If (ColourSpread=PATTERN_COLOR_MODE_STRIPED) Then Colour=(Iter Mod (COLOURS_SELECTION-1))+1
		SetTempBrick(Offset+Iter,PATTERN_BRICKS_H-(Iter+1),Colour*AddColour)
	Next
End Function	

Function SetTempBrick(X,Y,Colour)
	TempBrickArray(X,Y)=Colour
End Function

Function RepeatPattern()
	Local RepeatH=Rand(1,4)
	Local RepeatV=Rand(1,4)
	
	If ((RepeatV*RepeatH)=1) Then Return
	
	WriteLog("Applying pattern repetition table "+Str(RepeatH)+" x "+Str(RepeatV))
	
	Dim RepeatArray(PATTERN_BRICKS_W-1,PATTERN_BRICKS_H-1)
	
	Local StepX=Int(Floor(PATTERN_BRICKS_W/RepeatH))
	Local StepY=Int(Floor(PATTERN_BRICKS_H/RepeatV))
	
	Local Y
	Local X
	Local ModX
	Local ModY
	
	For Y = 0 To PATTERN_BRICKS_H- 1
		For X = 0 To PATTERN_BRICKS_W - 1
			ModX = X * RepeatH Mod PATTERN_BRICKS_W ;Calculate where in the source array we will get the data
			ModY = Y * RepeatV Mod PATTERN_BRICKS_H
			
			RepeatArray(X,Y) = TempBrickArray(ModX,ModY) ;Put it in the destination array
		Next
	Next
	
	For Y=0 To PATTERN_BRICKS_H-1
		For X=0 To PATTERN_BRICKS_W-1
			
			ModX=X
			ModY=Y
			
			;Mirror Symmetry - Deprecated
			;If (X>=(PATTERN_BRICKS_W*0.5))
			;	ModX=(PATTERN_BRICKS_W-1)-(X-0.5)
			;End If
			
			TempBrickArray(ModX,ModY)=RepeatArray(X,Y)
		Next
	Next
	Dim RepeatArray(0,0)
End Function

Function SelectBrickPattern()
	If (ROUND<=KNOWN_ROUNDS) And (HIGHEST_STAGE_REACHED<=ROUND)
			DefineKnownRoundPattern(ROUND)
		Else
			If (ROUND=0)
				DefineKnownRoundPattern(ROUND)
			Else
				SetRandomBrickPattern		
			End If
	End If
End Function

Function DefineKnownRoundPattern(Stage=0)
	Local Valid=False
	Select (Stage)
		Case 0:
			Valid=True
			Restore STAGE_0
			
		Case 1:
			Valid=True
			Restore STAGE_1
			
		Case 2:
			Valid=True
			Restore STAGE_2
			
		Case 3:
			Valid=True
			Restore STAGE_3
			
		Case 4:
			Valid=True
			Restore STAGE_4
			
		Default:
			;Should never occur, but just in case...
			SetRandomBrickPattern
	End Select
	
	If (Valid)
		WriteLog("This round uses pre-defined pattern #"+Str(Stage))
		ReadKnownPatternData
	End If
End Function

Function ReadKnownPatternData()
	WriteLog("Building pattern from known definition")
	Local Y
	Local X
	
	Local Colour
	
	For Y=0 To PATTERN_BRICKS_H-1
		For X=0 To PATTERN_BRICKS_W-1
			Read Colour
			PlaceBrick(X,Y,Colour)
		Next
	Next
End Function

Function RemoveImpossibleBricks()
	Local BR.BRICK_DATA
	Local ImpossibleBricks=False
	For BR= Each BRICK_DATA
		If (BR\BrickType=BRICK_INVINCIBLE)
			ImpossibleBricks=True
			DestroyBrick(BR)
		End If
	Next
	
	If (ImpossibleBricks)
		WriteLog("Removing impossible bricks due to timeout.")
		PlayBrickSound(SND_BRICK_IMPOSSIBLE_TIMEOUT)
	End If
	IMPOSSIBLE_BRICKS_REMOVED=True
End Function	

Function SpecialCaseAuditInvincibleImpossibility()
	Local X
	Local Y
	
	Local Colour
	Local Count
	
	For Y=1 To PATTERN_BRICKS_H-2
		For X=1  To PATTERN_BRICKS_W-2
			Colour=TempBrickArray(X,Y)
			
			If (Colour<COL_SELECTION_GREY)  And (Colour>0)
				Local BAbove=TempBrickArray(X,Y+1)
				If (BAbove=COL_SELECTION_GREY)
					Local BBelow=TempBrickArray(X,Y-1)
					If (BBelow=COL_SELECTION_GREY)
						Local BLeft=TempBrickArray(X-1,Y)
						If (BLeft=COL_SELECTION_GREY)
							Local BRight=TempBrickArray(X+1,Y)
							If (BRight=COL_SELECTION_GREY)
								TempBrickArray(X,Y)=0
								Count=Count+1
							End If
						End If
					End If
				End If
			End If
		Next
	Next
	
	If (Count)
		WriteLog("Removed "+Str(Count)+" impossible bricks")
	End If
	
End Function
;~IDEal Editor Parameters:
;~F#1D#44#4C#53#5F#78#8C#9F#B2#B6#135#199#1B6#1D1#1EC#1FF#212#222#23E#272
;~F#28F#2A1#2A5#2D1#2DD#2FF#30E#31F
;~C#Blitz3D