import { LitElement, html, css } from 'lit-element/lit-element';

export default class TreeView extends LitElement {

    static get properties() {
        return {
            data: { type: Object }
        };
    }

    constructor() {
        super();

        this.data = [];
        /*
        this.data = [
            {
                type: "folder",
                label: "a folder",
                children: [
                    {
                        type: "file",
                        label: "a file"
                    },
                    {
                        type: "folder",
                        label: "a folder",
                        children: [
                            {
                                type: "file",
                                label: "a file"
                            },
                            {
                                type: "file",
                                label: "b file"
                            }
                        ]
                    }
                ]
            },
            {
                type: "folder",
                label: "b folder",
                children: [
                    {
                        type: "file",
                        label: "ba file"
                    },
                    {
                        type: "folder",
                        label: "a asd asdasdd  asd",
                        children: [
                            {
                                type: "folder",
                                label: "b",
                                children: [
                                    {
                                        type: "folder",
                                        label: "c",
                                        children: [
                                            {
                                                type: "folder",
                                                label: "d",
                                                children: [
                                                    {
                                                        type: "file",
                                                        label: "deepfile.js"
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    }
                ]
            }
        ];*/

        this.selectedNode = null;
    }

    connectedCallback() {
        super.connectedCallback();

        this.expandLeafsByCondition((node) => node.label == 'a file');
    }

    render() {
        return html`
        <ul class="tree">
            ${this.data.map(node => this.renderNode(node))}
        </ul>
        `;
    }

    renderNode(node, meta) {
        const hasChildren = 'children' in node ? node.children.length > 0 : false;
        const isExpanded = 'expand' in node ? node.expand : false;
        const isSelected = this.selectedNode == node;
        const type = node.type;

        if (type == 'folder' && hasChildren && node.children.length == 1 && node.children[0].type == 'folder') {
            if (!meta) {
                meta = [];
            }
            return this.renderNode(node.children[0], [...meta, node])
        }

        return html`<div class="treenode-content" @click="${(e) => this.onNodeClicked(e, node)}">
            ${ type == 'folder' ? html`<span class="icon treenode-toggler-${isExpanded ? 'expanded' : 'collapsed'}"></span>` : html`<span class="icon treenode-toggler-none"></span>`}
            <span class="icon treenode-icon-${type}"></span>
            <span class="treenode-label ${isSelected ? `treenode-label-selected` : ``}">${this.renderLabel(node, meta)}</span>
            ${'hint' in node ? html`<span class="treenode-hint ${node.hint}">${node.hint}</span>` : html``}
            ${hasChildren && isExpanded ? html`<ul>${node.children.map(node => this.renderNode(node))}</ul>` : html``}
        </div>`;
    }

    renderLabel(node, meta) {
        meta = meta || [];
        return html`${meta.length > 0 ? meta.map(node => node.label).join(" / ") + " / " : ""}${node.label}`;
    }

    onNodeClicked(e, node) {
        if (!'expand' in node) {
            node.expand = true;
        }
        else {
            node.expand = !node.expand;
        }

        this.select(node);

        e.stopPropagation();
        this.requestUpdate();
    }

    select(node) {
        if (node.type == 'file') {
            this.selectedNode = node;

            const fileSelectedEvent = new CustomEvent("node-selected", {
                detail: node
            });
            this.dispatchEvent(fileSelectedEvent);
            return true;
        }
        else {
            return false;
        }
    }

    expandLeafsByCondition(filter) {
        this.data.forEach(node => this._expandLeafsByCondition(filter, node));
    }

    _expandLeafsByCondition(filter, node, acc) {
        if (acc === undefined) {
            acc = [];
        }
        acc.push(node);
        if ('children' in node) {
            // no leaf -> descend
            node.children.forEach(childNode => {
                this._expandLeafsByCondition(filter, childNode, [...acc, node]);
            });
        }
        else {
            // leaf
            if (filter(node)) {
                node.expand = true;
                acc.forEach(nodeInPath => nodeInPath.expand = true)
            }
        }
    }

    static get styles() {
        return css`
        
        
        
        
        `;
    }

    createRenderRoot() {
        return this;
    }
}

customElements.define('tree-view', TreeView);