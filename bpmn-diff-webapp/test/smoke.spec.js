/// <reference types="Cypress" />

context('smoke', () => {
  beforeEach(() => {
    cy.visit('http://localhost:3000')
  })

  // https://on.cypress.io/interacting-with-elements

  it('diff modified', () => {
    cy.get('a.file.Modified')
      .contains('another_modified.bpmn')
      .click();

    cy.get('div.io-control')
      .contains('List of Changes')
      .click();


    cy.get('#changes-overview')
    .find('tr')
    .then(rows => {
      //console.dir(rows[0])
      const changes = [];
      rows.each((i, row) => changes.push(row));
      changes.shift();

      expect(changes.length).to.equal(12);

      expect(changes[0].classList.contains('removed')).to.be.true
      expect(changes[0].children[1].textContent).to.equal('„where is my pizza?“')

      expect(changes[8].classList.contains('added')).to.be.true
      expect(changes[8].children[1].textContent).to.equal('what ever')

      expect(changes[10].classList.contains('changed')).to.be.true
      expect(changes[10].children[1].textContent).to.equal('Order Pasta!')

      expect(changes[11].classList.contains('layout-changed')).to.be.true
      expect(changes[11].children[1].textContent).to.equal('Hungry for pizza')

    });

    
    cy.get('#changes-overview')
    .find('td')
    .contains('Select a pizza')
    .click();

    cy.get('#changes-overview')
    .find('td')
    .contains('Order Pasta!')
    .scrollIntoView()
    .click();

    cy.get('#changes-overview')
    .find('td')
    .contains('Order Pasta!')
    .parent()
    .then(table => {
      const id = table.data('element');
      cy.get(`#changeDetailsOld_${id}`)
        .contains('Order Pasta!');
    });    
  })
})