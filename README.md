# minecraft-modreq
Plugin for minecraft that will add mod requests to the game (basically a ticketing system)

Idea inspired by other modreq plugins, but this plugin contains all my own work, nothing copied.

#v1.1.0 todo list
- [ x ] Add request escalation (escalated request require admins)
- [ x ] Add request abandonment (for mods who can't finish a request before they leave)
- [ ] Add request teleportation (for staff to tp to the location the request was sent)
- [ ] Add status for a users open request if they do /reqstatus (only applies if one status is open)
- [ ] Add modmode (turns select moderation utility commands on)
- [ x ] Implement [Plugin-Metrics](https://github.com/Hidendra/Plugin-Metrics)
- [ ] Port to Minecraft 1.10?


#Branches
Each main version will have it's own branch, both in development, and after releaese.
The main branch will always be the latest release version.

#Versioning
Each version will look like this:
x.y.z(-snapshot)
x is the majour version. This is likely to only increase when I do really big changes to the plugin (like updating to a majour minecraft release, or rewriting a significant portion of code)
y is the main version, this will change when I add new features, or rework the way something works in the backend.
z is the minor version, this will change after I release a bug fix version.
-snapshot is basically a pre-release version. Things are likely to be broken, or break. There aren't very likely to happen very often, unless I'm working on something big.

#License
Copyright 2016 GrumpyBear57 (Nathan Ford)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
