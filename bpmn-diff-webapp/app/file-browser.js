export default class FileBrowser extends HTMLElement {
    connectedCallback() {
        //this.root = this.attachShadow({mode: 'open'});
        this.selectedFileDomNode = null;
    }

    ls(files) {
        this.renderFiles(files, this);
    }

    select(selectedFile) {
        this._selectById(selectedFile.id);
    }

    _selectById(id) {
        if (this.selectedFileDomNode != null) {
            this.selectedFileDomNode.classList.toggle('selected');
        }
        this.selectedFileDomNode = document.getElementById(id);
        this.selectedFileDomNode.classList.toggle('selected');

        let fileSelectedEvent = new CustomEvent("file-selected", {
            detail: this.selectedFileDomNode.file
        });
        this.dispatchEvent(fileSelectedEvent);
    }

    renderFiles(files, parent) {
        const ul = document.createElement('ul');
        ul.classList.add('fa-ul');
        for (const file of files) {
            this.renderFile(file, ul);
        }
        parent.appendChild(ul);
    }

    renderFile(file, parent) {

        const isDirectory = file.children != null;
        const icon = isDirectory ? 'fa-folder' : 'fa-file';
        // file
        const li = document.createElement('li');
        if (!isDirectory) {
            li.id = file.id;
        }
        li.file = file;
        li.innerHTML = `<span class="fa-li" ><i class="far ${icon}"></i></span><a href="#">${file.leftName}</a>`;
        li.querySelector('a').onclick = e => {
            const li = e.target.parentElement;
            if (li.id == "") return false;
            this._selectById(li.id);
            return false;
        }

        parent.appendChild(li);


        if (isDirectory) {
            // folder
            this.renderFiles(file.children, li);
        }
        
    }

}

customElements.define('a-file-browser', FileBrowser);