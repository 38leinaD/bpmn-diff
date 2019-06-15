export default class FileBrowser extends HTMLElement {
    static get observedAttributes() {
        return ['collapseEmpty'];
    }

    connectedCallback() {
        //this.root = this.attachShadow({mode: 'open'});
        this.selectedFileDomNode = null;
        this.innerHTML = `<h2>Files</h2>`;
    }

    ls(files) {
        this.renderFiles(files, this);

        //const hr = document.createElement('hr');
        //this.appendChild(hr);
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

    isDirectory(f) {
        return f.children != null;
    }

    resolveNameOfFile(file) {
        if (file.type == "Removed") {
            return file.leftName;
        }
        else {
            return file.rightName;
        }
    }

    renderFile(file, parent) {

        let tip = file;
        let collapsedPath = this.resolveNameOfFile(tip);
        while (this.collapseEmpty && this.isDirectory(tip) && tip.children != null && tip.children.length == 1) {
            tip = tip.children[0];
            collapsedPath += `/${this.resolveNameOfFile(tip)}`;
        }

        // file
        const li = document.createElement('li');
        if (!this.isDirectory(tip)) {
            li.id = tip.id;
        }
        const icon = this.isDirectory(tip) ? 'fa-folder' : 'fa-file';

        li.file = tip;
        li.innerHTML = `<span class="fa-li" ><i class="far ${icon}"></i></span><a href="#" class="file ${tip.type}">${collapsedPath}</a>`;
        li.querySelector('a').onclick = e => {
            const li = e.target.parentElement;
            if (li.id == "") return false;
            this._selectById(li.id);
            return false;
        }

        parent.appendChild(li);


        if (this.isDirectory(tip)) {
            // folder
            this.renderFiles(tip.children, li);
        }
        
    }

    get collapseEmpty() {
        return this.getAttribute('collapseEmpty') == 'true';
    }

    attributeChangedCallback(attributeName, oldValue, newValue) {
        //console.log("@@@@@ " + attributeName + ": " + newValue)
    }
}

customElements.define('a-file-browser', FileBrowser);