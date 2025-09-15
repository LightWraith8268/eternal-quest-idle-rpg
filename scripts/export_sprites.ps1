Param(
  [string]$OutDir = "exports"
)

$ErrorActionPreference='Stop'
$root = Get-Location
$outPath = Join-Path $root $OutDir
if(-not (Test-Path $outPath)) { New-Item -ItemType Directory -Path $outPath | Out-Null }

Function Parse-File {
  Param([string]$Path, [string]$Pattern, [string]$Type)
  if(-not (Test-Path $Path)) { return @() }
  $content = Get-Content -Path $Path -Raw
  $rx = [regex]$Pattern
  $rows = @()
  foreach($m in $rx.Matches($content)){
    $obj = [pscustomobject]@{ type=$Type; id=$m.Groups[1].Value; name=$m.Groups[2].Value }
    if($m.Groups.Count -ge 4){ $obj | Add-Member -NotePropertyName areaType -NotePropertyValue $m.Groups[3].Value }
    $rows += $obj
  }
  return $rows
}

$items = Parse-File 'app/src/main/java/com/eternalquest/data/entities/Item.kt' 'val\s+[A-Z0-9_]+\s*=\s*Item\(\s*"([^"]+)",\s*"([^"]+)"' 'item'
$enemies = Parse-File 'app/src/main/java/com/eternalquest/data/entities/Combat.kt' 'val\s+[A-Z0-9_]+\s*=\s*Enemy\(\s*"([^"]+)",\s*"([^"]+)"' 'enemy'
$areas = Parse-File 'app/src/main/java/com/eternalquest/data/entities/Combat.kt' 'val\s+[A-Z0-9_]+\s*=\s*GameArea\(\s*"([^"]+)",\s*"([^"]+)",\s*AreaType\.([A-Z_]+)' 'area'
$skills = Parse-File 'app/src/main/java/com/eternalquest/data/entities/Skill.kt' 'val\s+[A-Z0-9_]+\s*=\s*SkillType\(\s*"([^"]+)",\s*"([^"]+)"' 'skill'
$activities = Parse-File 'app/src/main/java/com/eternalquest/data/entities/ActivityState.kt' 'val\s+[A-Z0-9_]+\s*=\s*ActivityDefinition\(\s*"([^"]+)",\s*"([^"]+)"' 'activity'
# Also include JSON-defined activities (recipes.json)
try {
  $recipes = 'app/src/main/assets/config/recipes.json'
  if(Test-Path $recipes){
    $json = Get-Content -Path $recipes -Raw | ConvertFrom-Json
    if($json.activities){
      foreach($a in $json.activities){
        if($a.id){ $activities += [pscustomobject]@{ type='activity'; id=$a.id; name=($a.name ? $a.name : $a.id) } }
      }
    }
  }
} catch { Write-Host "Warning: Failed to parse recipes.json: $($_.Exception.Message)" }

$all = @() + $items + $enemies + $areas + $skills + $activities | Sort-Object type,id

$txtPath = Join-Path $outPath 'sprite_targets.txt'
$csvPath = Join-Path $outPath 'sprite_targets.csv'
$jsonPath = Join-Path $outPath 'sprite_targets.json'

$all | ForEach-Object { "$($_.type)`t$($_.id)`t$($_.name)`t$($_.areaType)" } | Out-File -FilePath $txtPath -Encoding utf8
$all | Select-Object type,id,name,areaType | Export-Csv -NoTypeInformation -Path $csvPath -Encoding UTF8
$all | ConvertTo-Json -Depth 4 | Out-File -FilePath $jsonPath -Encoding utf8

$all | Group-Object type | ForEach-Object { Write-Host ("Type=$($_.Name) Count=$($_.Count)") }
Write-Host ("Exported $($all.Count) entries to $outPath")
