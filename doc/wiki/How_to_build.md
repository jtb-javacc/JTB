# Java Tree Builder (JTB) - How to build and deploy

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

## GitHub environment

### Account & repos

[jtb-javacc](https://github.com/jtb-javacc) is a public organization account owned by me.  
You can ask to be a member of it (even an owner of it).  

It holds the [JTB](https://github.com/jtb-javacc/JTB) repository along with some related ones.  

### How to set GitHub ssh keys

On your (member / owner) account, you need to add a proper SSH key to access the repos.  

Follow [generating-a-new-ssh-key](https://docs.github.com/fr/authentication/connecting-to-github-with-ssh/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent)  
then [adding-a-new-ssh-key](https://docs.github.com/fr/authentication/connecting-to-github-with-ssh/adding-a-new-ssh-key-to-your-github-account)  
then [testing-your-ssh-connection](https://docs.github.com/fr/authentication/connecting-to-github-with-ssh/testing-your-ssh-connection).  

