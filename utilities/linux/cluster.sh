#!/bin/bash

#bin=`dirname "$0"`
#bin=`cd "$bin"; pwd`
####################################################################
MASTER="hadoop1.saarus.org"
SLAVES="hadoop2.saarus.org hadoop3.saarus.org"

SSH_KEY="/home/saarus/.ssh/id_rsa"
INSTALL_DIR="/opt/saarus"
CURRENT_DIR=`pwd`
####################################################################

COMMAND=$1
shift


MEMBERS="$MASTER $SLAVES"

function cluster_exec() {
  for worker in $MEMBERS; do
    echo "#############################################################################"
    echo "Execute '$@' On  $worker by $USER"
    echo "#############################################################################"
    ssh -i $SSH_KEY $USER@$worker "$@"
  done
}


function cluster_exec_slaves() {
  for worker in $SLAVES; do
    echo "#############################################################################"
    echo "Execute '$@' On  $worker" by $USER
    echo "#############################################################################"
    ssh -i $SSH_KEY $USER@$worker "$@"
  done
}

function cluster_scp() {
  SRC="$1"
  case "$SRC" in
  */)
    SRC="${SRC%?}"
    ;;
  esac

  REMOTE_DEST="$2"

  for worker in $SLAVES; do
    echo "###########################################################"
    echo "copy data $SRC to $worker:$REMOTE_DEST"
    echo "###########################################################"
    scp -i $SSH_KEY -r $SRC $USER@$worker:$REMOTE_DEST
  done
}

function cluster_rsync() {
  DIR="$1"
  case "$DIR" in
  */)
    DIR="${DIR%?}"
    ;;
  esac

  REMOTE_DIR="$2"

  for worker in $SLAVES; do
    echo "###########################################################"
    echo "synchronized data $DIR with $worker"
    echo "###########################################################"
    rsync -e "ssh -i $SSH_KEY" -vr --delete $DIR $USER@$worker:$REMOTE_DIR
  done
}

function confirmYN() {
 while true; do
    read -p "$@" yn
    case $yn in
      [Yy]* ) break;;
      [Nn]* ) exit;;
      * ) echo "Please answer yes or no.";;
    esac
  done
}

printf "\n\n\n"

if [ "$COMMAND" = "exec" ] ; then
  cluster_exec $@
elif [ "$COMMAND" = "rsync" ] ; then
  confirmYN "Do you want to sync $1 with $2 on the remote members(Y/N)?"
  cluster_rsync $@
else
  echo "cluster command options: "
  echo "  exec      : To execute the shell command on all the members"
  echo "  rsync     : To copy this program to the members"
fi

printf "\n\n\n"
