package com.tesobe.obp.mar2017

/**
  * Model based on example_import_mar2017.json
  *
  */

case class DriveUp(
                    hours: Option[String]
                  )

case class License(
                    id: Option[String],
                    name: Option[String]
                  )

case class Meta(
                 license: License
               )

case class Location(
                     latitude: Double,
                     longitude: Double
                   )

case class Address(
                    city: Option[String],
                    countryCode: Option[String],
                    line3: Option[String],
                    line2: Option[String],
                    line1: Option[String],
                    county: Option[String],
                    state: Option[String],
                    postCode: Option[String]
                  )

case class Branch(
                   name: Option[String],
                   bankId: Option[String],
                   id: Option[String],
                   driveUp: DriveUp,
                   meta: Meta,
                   location: Location,
                   address: Address,
                   lobby: DriveUp
                 )

case class FxRate(
                   inverseConversionValue: Double,
                   toCurrencyCode: Option[String],
                   conversionValue: Double,
                   fromCurrencyCode: Option[String],
                   effectiveDate: Option[String]
                 )

case class Atm(
                name: Option[String],
                bankId: Option[String],
                meta: Meta,
                location: Location,
                address: Address,
                id: Option[String]
              )

case class Balance(
                    currency: Option[String],
                    amount: Option[String]
                  )

case class Account(
                    owners: List[String],
                    generatePublicView: Boolean,
                    generateAuditorsView: Boolean,
                    number: Option[String],
                    label: Option[String],
                    IBAN: Option[String],
                    generateAccountantsView: Boolean,
                    balance: Balance,
                    `type`: Option[String],
                    id: Option[String],
                    bank: Option[String],
                    branchId: Option[String],
                    accountRoutingAddress: Option[String],
                    accountRoutingScheme: Option[String]
                  )

case class Customer(
                     name: Option[String],
                     number: Option[String]
                   )

case class CrmEvent(
                     customer: Customer,
                     category: Option[String],
                     actualDate: Option[String],
                     detail: Option[String],
                     bankId: Option[String],
                     id: Option[String],
                     channel: Option[String]
                   )

case class Bank(
                 website: Option[String],
                 logo: Option[String],
                 fullName: Option[String],
                 shortName: Option[String],
                 id: Option[String]
               )

case class Product(
                    category: Option[String],
                    superFamily: Option[String],
                    code: Option[String],
                    name: Option[String],
                    family: Option[String],
                    meta: Meta,
                    bankId: Option[String],
                    moreInfoUrl: Option[String]
                  )

case class Counterparty(
                         thisViewId: Option[String],
                         otherBankRoutingScheme: Option[String],
                         thisBankId: Option[String],
                         thisAccountId: Option[String],
                         otherBranchRoutingAddress: Option[String],
                         otherAccountRoutingAddress: Option[String],
                         otherBankRoutingAddress: Option[String],
                         createdByUserId: Option[String],
                         counterpartyId: Option[String],
                         otherAccountRoutingScheme: Option[String],
                         otherBranchRoutingScheme: Option[String],
                         isBeneficiary: Boolean,
                         name: Option[String]
                       )

case class ThisAccount(id: Option[String],
                       bank: Option[String]
                      )
case class CounterPartySimple(name: Option[String],
                              id: Option[String]
                             )
case class Transaction(id: Option[String],
                       thisAccount: Option[ThisAccount],
                       counterparty: Option[CounterPartySimple],
                       userId: Option[String],
                       transactionChargePolicy: Option[String],
                       toCounterpartyBankRoutingScheme: Option[String],
                       toCounterpartyBankRoutingAddress: Option[String],
                       toCounterpartyName: Option[String],
                       transactionPostedDate: Option[String],
                       toCounterpartyRoutingAddress: Option[String],
                       fromAccountBankId: Option[String],
                       transactionRequestType: Option[String],
                       toCounterpartyCurrency: Option[String],
                       `type`: Option[String],
                       username: Option[String],
                       transactionChargeAmount: Option[String],
                       transactionDescription: Option[String],
                       transactionCurrency: Option[String],
                       fromAccountId: Option[String],
                       transactionAmount: Option[String],
                       toCounterpartyId: Option[String],
                       toCounterpartyRoutingScheme: Option[String],
                       fromAccountName: Option[String],
                       transactionChargeCurrency: Option[String],
                       transactionId: Option[String]
                      )

case class TransactionRequestType(
                                   chargeSummary: Option[String],
                                   createDate: Option[String],
                                   id: Option[String],
                                   updateDate: Option[String],
                                   bankId: Option[String],
                                   chargeCurrency: Option[String],
                                   chargeAmount: Option[String],
                                   transactionRequestType: Option[String]
                                 )

case class Role(validationDate: Option[String],
                name: Option[String]
               )

case class User(password: Option[String],
                displayName: Option[String],
                email: Option[String],
                roles: Option[Seq[Role]]
               )

case class Example(
                    branches: List[Branch],
                    users: Seq[User],
                    transactions: Seq[Transaction],
                    fxRates: List[FxRate],
                    atms: List[Atm],
                    accounts: List[Account],
                    crmEvents: List[CrmEvent],
                    banks: List[Bank],
                    products: List[Product],
                    counterparties: List[Counterparty],
                    transactionRequestTypes: List[TransactionRequestType]
                  )