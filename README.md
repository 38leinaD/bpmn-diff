git config --global difftool.bpmn "java -jar /home/daniel/dev/bpmn-diff/bpmn-diff-cli/build/libs/bpmn-diff-cli.jar $LOCAL $REMOTE"
git difftool -t bpmn HEAD~1 HEAD flow.bpmn