#!/bin/bash

function remountRunDir() {
  mount -o rw,nosuid,exec,relatime,remount,size=209716k,mode=755 -t tmpfs none /run
}

function addUser() {
  # -s SHELL : Login shell for the user.
  # -m : Create user’s home directory if it does not exist.
  # -d HomeDir : Home directory of the user.
  # -g Group : Group name or number of the user.
  # UserName : Login id of the user.

  read -p "$@" "Enter the user name: " user
  read -p "$@" "Enter the group name separated by comma(admin,hadoop,sudo..): " groups

  #create user
  useradd -s /bin/bash -m -d /home/$user -G $groups $user

  #change password for the new user
  passwd $user
}

COMMAND=$1
shift

if [ "$COMMAND" = "remountRunDir" ] ; then
  remountRunDir
elif [ "$COMMAND" = "addUser" ] ; then
  addUser $@
else
  echo "Available commands: "
  echo "  remountRunDir "
  echo "  addUser "
fi
