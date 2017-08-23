function fileQueueError(file, errorCode, message) {
    try {
        var imageName = "error.gif";
        var errorName = "";
        if (errorCode === SWFUpload.errorCode_QUEUE_LIMIT_EXCEEDED) {
            errorName = lang.get('queue_too_many');
        }

        if (errorName !== "") {
            alert(errorName);
            return;
        }

        switch (errorCode) {
        case SWFUpload.QUEUE_ERROR.ZERO_BYTE_FILE:
            imageName = "zerobyte.gif";
            break;
        case SWFUpload.QUEUE_ERROR.FILE_EXCEEDS_SIZE_LIMIT:
            file.kb = file.size/1024;
            file.kb = file.kb.toFixed(2);
            alert(file.name + '(' + file.kb + ' KB)' +'\n' + lang.get('not_allowed_size'));
            imageName = "toobig.gif";
            break;
        case SWFUpload.QUEUE_ERROR.ZERO_BYTE_FILE:
        case SWFUpload.QUEUE_ERROR.INVALID_FILETYPE:
        case SWFUpload.UPLOAD_ERROR.FILE_CANCELLED:
            progress.setStatus(lang.canceled);
            progress.setCancelled();
            break;
        default:
            alert(message);
            break;
        }

        //addImage("images/" + imageName);

    } catch (ex) {
        this.debug(ex);
    }

}

function fileQueued(file) {
    try {
        // You might include code here that prevents the form from being submitted while the upload is in
        // progress.  Then you'll want to put code in the Queue Complete handler to "unblock" the form
        var progress = new FileProgress(file, this.customSettings);
        progress.setStatus(lang.pending);
        progress.toggleCancel(true, this);

    } catch (ex) {
        this.debug(ex);
    }

}

function fileDialogComplete(numFilesSelected, numFilesQueued) {
    try {
        if (numFilesQueued > 0) {
        	window.numFilesQueued = numFilesQueued;
            this.startUpload();
        }
    } catch (ex) {
        this.debug(ex);
    }
}

function uploadProgress(file, bytesLoaded) {

    try {
        var percent = Math.ceil((bytesLoaded / file.size) * 100);
        var progress = new FileProgress(file,  this.customSettings);
        progress.setProgress(percent);
        if (percent === 100) {
            progress.toggleCancel(false, this);
        } else {
            progress.setStatus(lang.uploading + percent + "%");
            progress.toggleCancel(true, this);
        }
    } catch (ex) {
        this.debug(ex);
    }
}

function uploadSuccess(file, serverData) {
	var progress = new FileProgress(file,  this.customSettings);
	add_uploadedfile(serverData);
	progress.setStatus('ok');
	progress.setComplete();
	progress.toggleCancel(false);
	window.numFilesQueued--
	if(window.numFilesQueued==0){upload_complete();}
}

function uploadComplete(file) {
    try {
        /*  I want the next upload to continue automatically so I'll call startUpload here */
        if (this.getStats().files_queued > 0) {
            //document.getElementById(this.customSettings.cancelButtonId).disabled = false;

        }
        this.startUpload();
    } catch (ex) {
        this.debug(ex);
    }
}

function uploadError(file, errorCode, message) {
    var imageName =  "error.gif";
    var progress;
    try {
        switch (errorCode) {
        case SWFUpload.UPLOAD_ERROR.FILE_CANCELLED:
            try {
                progress = new FileProgress(file,  this.customSettings);
                progress.setCancelled();
                progress.setStatus('error');
                progress.toggleCancel(false);
            }
            catch (ex1) {
                this.debug(ex1);
            }
            break;
        case SWFUpload.UPLOAD_ERROR.UPLOAD_STOPPED:
            try {
                progress = new FileProgress(file,  this.customSettings);
                progress.setCancelled();
                progress.setStatus(lang.stopped);
                progress.toggleCancel(true);
            }
            catch (ex2) {
                this.debug(ex2);
            }
        case SWFUpload.UPLOAD_ERROR.UPLOAD_LIMIT_EXCEEDED:
            imageName = "uploadlimit.gif";
            break;
        default:
            alert(message);
            break;
        }

        //addImage("images/" + imageName);

    } catch (ex3) {
        this.debug(ex3);
    }

}

/* ******************************************
 *    FileProgress Object
 *    Control object for displaying file info
 * ****************************************** */

function FileProgress(file, customSettings) {

    var targetID = customSettings.upload_target;
    var if_multirow = customSettings.if_multirow ? customSettings.if_multirow : 0;
    this.fileProgressID = file.id;
    if(if_multirow){
        var fileProgressContainerID = this.fileProgressID;
    }else{
        var fileProgressContainerID = "divFileProgress";
    }
    this.opacity = 100;
    this.height = 0;
    this.fileProgressWrapper = document.getElementById(fileProgressContainerID);

    if (!this.fileProgressWrapper) {
        this.fileProgressWrapper = document.createElement("div");
        this.fileProgressWrapper.className = "progressWrapper";
        this.fileProgressWrapper.id = fileProgressContainerID;

        this.fileProgressElement = document.createElement("div");
        this.fileProgressElement.className = "progressContainer";

        var progressCancel = document.createElement("a");
        progressCancel.className = "progressCancel";
        progressCancel.href = "#";
        progressCancel.style.visibility = "hidden";
        progressCancel.appendChild(document.createTextNode(" "));

        var progressText = document.createElement("div");
        progressText.className = "progressName";
        progressText.appendChild(document.createTextNode(file.name));

        var progressBar = document.createElement("div");
        progressBar.className = "progressBarInProgress";

        var progressStatus = document.createElement("div");
        progressStatus.className = "progressBarStatus";
        progressStatus.innerHTML = "&nbsp;";

        this.fileProgressElement.appendChild(progressCancel);
        this.fileProgressElement.appendChild(progressText);
        this.fileProgressElement.appendChild(progressStatus);
        this.fileProgressElement.appendChild(progressBar);

        this.fileProgressWrapper.appendChild(this.fileProgressElement);

        document.getElementById(targetID).appendChild(this.fileProgressWrapper);

    } else {
        this.fileProgressElement = this.fileProgressWrapper.firstChild;
        this.fileProgressElement.childNodes[1].firstChild.nodeValue = file.name;
        this.reset();
    }

    this.height = this.fileProgressWrapper.offsetHeight;
    this.setTimer(null);

}
FileProgress.prototype.setProgress = function (percentage) {
    this.fileProgressElement.className = "progressContainer swfupload_green";
    this.fileProgressElement.childNodes[3].className = "progressBarInProgress";
    this.fileProgressElement.childNodes[3].style.width = percentage + "%";

    this.appear();
};
FileProgress.prototype.reset = function () {
    this.fileProgressElement.className = "progressContainer";

    this.fileProgressElement.childNodes[2].innerHTML = "&nbsp;";
    this.fileProgressElement.childNodes[2].className = "progressBarStatus";

    this.fileProgressElement.childNodes[3].className = "progressBarInProgress";
    this.fileProgressElement.childNodes[3].style.width = "0%";

    this.appear();
};
FileProgress.prototype.setTimer = function (timer) {
    this.fileProgressElement["FP_TIMER"] = timer;
};
FileProgress.prototype.getTimer = function (timer) {
    return this.fileProgressElement["FP_TIMER"] || null;
};
FileProgress.prototype.setComplete = function () {
    this.fileProgressElement.className = "progressContainer swfupload_blue";
    this.fileProgressElement.childNodes[3].className = "progressBarComplete";
    this.fileProgressElement.childNodes[3].style.width = "";

    var oSelf = this;
    this.setTimer(setTimeout(function () {
        oSelf.disappear();
    }, 3000));

};
FileProgress.prototype.setError = function () {
    this.fileProgressElement.className = "progressContainer swfupload_red";
    this.fileProgressElement.childNodes[3].className = "progressBarError";
    this.fileProgressElement.childNodes[3].style.width = "";

    var oSelf = this;
    this.setTimer(setTimeout(function () {
        oSelf.disappear();
    }, 5000));

};
FileProgress.prototype.setCancelled = function () {
    this.fileProgressElement.className = "progressContainer";
    this.fileProgressElement.childNodes[3].className = "progressBarError";
    this.fileProgressElement.childNodes[3].style.width = "";

    var oSelf = this;
    this.setTimer(setTimeout(function () {
        oSelf.disappear();
    }, 2000));

};
FileProgress.prototype.setStatus = function (status) {
    this.fileProgressElement.childNodes[2].innerHTML = status;
};

FileProgress.prototype.toggleCancel = function (show, swfuploadInstance) {//alert(this.fileProgressID);
    this.fileProgressElement.childNodes[0].style.visibility = show ? "visible" : "hidden";
    if (swfuploadInstance) {
        var fileID = this.fileProgressID;
        $.each(this,function(i,u){
            //alert(i+ '\n' +u);
        });
        this.fileProgressElement.childNodes[0].onclick = function () {
            swfuploadInstance.cancelUpload(fileID);
            return false;
        };
    }
};
FileProgress.prototype.appear = function () {
    if (this.getTimer() !== null) {
        clearTimeout(this.getTimer());
        this.setTimer(null);
    }

    if (this.fileProgressWrapper.filters) {
        try {
            this.fileProgressWrapper.filters.item("DXImageTransform.Microsoft.Alpha").opacity = 100;
        } catch (e) {
            // If it is not set initially, the browser will throw an error.  This will set it if it is not set yet.
            this.fileProgressWrapper.style.filter = "progid:DXImageTransform.Microsoft.Alpha(opacity=100)";
        }
    } else {
        this.fileProgressWrapper.style.opacity = 1;
    }

    this.fileProgressWrapper.style.height = "";

    this.height = this.fileProgressWrapper.offsetHeight;
    this.opacity = 100;
    this.fileProgressWrapper.style.display = "";

};

// Fades out and clips away the FileProgress box.
FileProgress.prototype.disappear = function () {

    var reduceOpacityBy = 15;
    var reduceHeightBy = 4;
    var rate = 30;    // 15 fps

    if (this.opacity > 0) {
        this.opacity -= reduceOpacityBy;
        if (this.opacity < 0) {
            this.opacity = 0;
        }

        if (this.fileProgressWrapper.filters) {
            try {
                this.fileProgressWrapper.filters.item("DXImageTransform.Microsoft.Alpha").opacity = this.opacity;
            } catch (e) {
                // If it is not set initially, the browser will throw an error.  This will set it if it is not set yet.
                this.fileProgressWrapper.style.filter = "progid:DXImageTransform.Microsoft.Alpha(opacity=" + this.opacity + ")";
            }
        } else {
            this.fileProgressWrapper.style.opacity = this.opacity / 100;
        }
    }

    if (this.height > 0) {
        this.height -= reduceHeightBy;
        if (this.height < 0) {
            this.height = 0;
        }

        this.fileProgressWrapper.style.height = this.height + "px";
    }

    if (this.height > 0 || this.opacity > 0) {
        var oSelf = this;
        this.setTimer(setTimeout(function () {
            oSelf.disappear();
        }, rate));
    } else {
        this.fileProgressWrapper.style.display = "none";
        this.setTimer(null);
    }
};
