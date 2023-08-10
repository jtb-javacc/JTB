# # Java Tree Builder (JTB) -How to build and deploy

(for project admins) - Marc Mazas - Jan 13th, 2017

## Build a new JTB version

- Check / update doc/a_faire_jtb.txt
- Update doc/wiki/Releases.notes.md
- Update other wiki pages doc/wiki if needed
- Update versions: see comments in build.xml: set paths & versions in the 4 different files
- Build: see comments in build.xml: generate and rename to jtb-x.y.z.jar
- Commit to the local repository 

## Deploy to GitHub the new JTB version

- Check doc & wiki updates
- Push master
- Update wiki pages (edit & copy/paste the changed ones)

## Deploy the new JTB version to the JavaCC Eclipse plugin at SourceForge 

- Copy the last jar to the local plugin project lib folder (`sf.eclipse.javacc.core/lib`)
- See the plugin README to set the version (`sf.eclipse.javacc.site`)

## Deploy the new JTB version to Maven Central

TODO (FA)

