HOW TO BUILD (GitHub, no installs)
1. Create a free account at github.com and click New repository (name: confis-client, Public or Private).
2. On the new repo page click "uploading an existing file", drag in EVERYTHING from this folder
   (including the hidden .github folder) and click Commit changes.
   Tip: easiest is to drag the folders themselves: .github, intermediary-src, src, tools plus the
   loose files build.gradle, gradle.properties, settings.gradle.
3. Click the Actions tab -> "Build mod" -> wait ~3-6 minutes for a green check.
4. Open the finished run -> scroll to Artifacts -> download confis-mod-jar -> unzip -> put the .jar
   in .minecraft/mods (Fabric Loader + Fabric API required).
If the run is red, open it, copy the red error text, and send it to Claude.

WHAT'S NEW IN THIS VERSION (all show up as modules when you press Right Shift)
- Inventory Hub, Totem Count, Potion Count, Saturation, Zoom (key changeable in its options), Tier Tagger
  (PvPTiers / MCTiers / both), Shulker Tooltip (hold Shift over a shulker box), Crosshair Indicator.
- Effect Timer now shows the effect icon instead of the effect name.
- Item Guide: click an item to see its crafting recipe and where to find it.
- Name badge: "C" before your own name; other mod users get it too if you set "userListUrl" in
  .minecraft/config/confis0oooooo.json to a text file (one player name per line).
- Tier tagger web addresses are in the same config file (tierUrlMc / tierUrlPvp) in case a site changes its API.

UPDATE (HUD module changes)
- Freecam (new HUD module): press F6 in game to fly the camera around while your player stays put.
  Change the key and speed in Freecam options. Fly with W A S D / Space / Shift (Ctrl = faster).
- Armor Status: options now let you show durability as a Number, a Percentage, or Both,
  and put that text on the Left or Right of the item.
- Potion Count: already in the module list (counts potions, splash and lingering).
- Minimap removed (module, settings button and code). Waypoints still work.
- Fullbright (new): switch on in the module list; brightness goes to max and is restored when you switch it off.
- No Blindness (new): removes the Blindness effect view. Both start switched OFF.

UPDATE 3
- Item Drop Scale / Online Players: the search box keeps focus while you type.
- Hitboxes & Nametags: switches now work on 1.21.11 (names were using the wrong mapping before).
- Messenger replaced by Friends (Tools > Friends): type a name + ADD, click a friend to chat (uses /msg).
  Friends listed in userListUrl show a blue C. Green dot = online.
- Right Shift menu now pops up with a short animation.
- Edit HUD has no blur.
- Server TPS fixed (hooks the server time packet).

UPDATE 4 (Liquid Glass HUD)
- Every HUD module (FPS, CPS, ping, keystrokes, armor, inventory hub, effects, scoreboard, counters ...) now uses
  an iOS-style liquid glass look: rounded frosted panel, bright sheen on top, glowing rim that fades down,
  soft shadow. Inventory slots are rounded glass wells.
- The module's "background" color and the background switch still work: the color's transparency sets how frosted
  the glass is, and turning the background off hides the glass completely.
- New file: intermediary-src/client/confis/Glass.java (upload it with the other folders).

UPDATE 5 (Liquid Glass menu)
- The Right Shift menu (All Modules, tabs, Options, Waypoints, Settings) now has the same liquid glass style:
  see-through rounded window, glass module cards that glow brighter when you hover them or switch them on,
  pill buttons (the active tab and "Enabled" are light glass), inset glass search box, rounded sliders and scrollbar.
- Changed files: Glass.java, ConfigScreen.java (both in intermediary-src/client/confis).
- Also includes the fixes for ItemGuideScreen.java and ShulkerPreview.java.

UPDATE 6 (Item Guide search)
- Typing in the Item Guide search box now works: the box keeps focus while the list refreshes after each letter.
- Shows "No items found." when nothing matches. Changed file: ItemGuideScreen.java.

UPDATE 7
- Inventory Hub options: new "Quick Access Bar" ON/OFF button (hides the bottom hotbar row and shrinks the box).
- Big FPS fix for the Right Shift menu: the liquid glass now merges rows into far fewer draw calls
  (about 4x fewer), and the key-rebind scan only runs while you are rebinding a key.
- Category tabs (ALL / INFO / HUD ...) play the vanilla button click sound.
- Edit HUD: click an element and use the - / + buttons under it, or scroll the mouse wheel over it, to change its size.
- Changed files: Glass.java, ConfigScreen.java, NewModules.java, Config.java.

UPDATE 8 (Neon blue glass menu, no blur)
- Menu restyled after the reference image: translucent blue glass window, bright cyan-white rims and sheen,
  blue glass cards/tabs/buttons, green "Enabled" pills, white hearts, colored pixel icons.
- The menu no longer blurs or dims the game (no full-screen effects). Glass is flat fills + banded gradients only.
- Changed files: Glass.java, Icons.java, ConfigScreen.java.
