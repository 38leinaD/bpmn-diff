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

    iconFor(file) {
        if (this.isDirectory(file)) {
            return `<svg class="file-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512"><path d="M464 128H272l-54.63-54.63c-6-6-14.14-9.37-22.63-9.37H48C21.49 64 0 85.49 0 112v288c0 26.51 21.49 48 48 48h416c26.51 0 48-21.49 48-48V176c0-26.51-21.49-48-48-48zm0 272H48V112h140.12l54.63 54.63c6 6 14.14 9.37 22.63 9.37H464v224z"/></svg>`;
        }
        else {
            return `<svg class="file-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 384 512"><path d="M369.9 97.9L286 14C277 5 264.8-.1 252.1-.1H48C21.5 0 0 21.5 0 48v416c0 26.5 21.5 48 48 48h288c26.5 0 48-21.5 48-48V131.9c0-12.7-5.1-25-14.1-34zM332.1 128H256V51.9l76.1 76.1zM48 464V48h160v104c0 13.3 10.7 24 24 24h104v288H48z"/></svg>`;
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

        if (this.isDirectory(tip)) {
            // folder

            li.innerHTML = `<span class="fa-li" >${this.iconFor(file)}</span><a href="#" class="file ${tip.type}">${collapsedPath}</a>`;
            parent.appendChild(li);

            this.renderFiles(tip.children, li);
        }
        else if (!tip.supported) {
            li.innerHTML = `<span class="fa-li" >${this.iconFor(file)}</span><a class="file ${tip.type} unknown-format">${collapsedPath}</a> (unknown format)`;
            parent.appendChild(li);
        }
        else {
            // file
            li.innerHTML = `<span class="fa-li" >${this.iconFor(file)}</span><a href="#" class="file ${tip.type}">${collapsedPath}</a>`;
            li.querySelector('a').onclick = e => {
                const li = e.target.parentElement;
                if (li.id == "") return false;
                this._selectById(li.id);
                return false;
            }

            parent.appendChild(li);
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