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

    cy.get('td')
      .contains('Select a pizza')
      .click();
  })
})
