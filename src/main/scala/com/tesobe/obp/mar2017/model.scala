package com.tesobe.obp.mar2017

/**
  * Created by slavisa on 6/8/17.
  */

case class DriveUp(
                    hours: String
                  )
case class License(
                    id: String,
                    name: String
                  )
case class MetaBis(
                    license: License
                  )
case class Location(
                     latitude: Double,
                     longitude: Double
                   )
case class Address(
                    city: String,
                    countryCode: String,
                    line3: String,
                    line2: String,
                    line1: String,
                    county: String,
                    state: String,
                    postCode: String
                  )
case class Branches(
                     name: String,
                     bankId: String,
                     id: String,
                     driveUp: DriveUp,
                     meta: MetaBis,
                     location: Location,
                     address: Address,
                     lobby: DriveUp
                   )
case class FxRates(
                    inverseConversionValue: Double,
                    toCurrencyCode: String,
                    conversionValue: Double,
                    fromCurrencyCode: String,
                    effectiveDate: String
                  )
case class Atms(
                 name: String,
                 bankId: String,
                 meta: MetaBis,
                 location: Location,
                 address: Address,
                 id: String
               )
case class Balance(
                    currency: String,
                    amount: String
                  )
case class Accounts(
                     owners: List[String],
                     generatePublicView: Boolean,
                     generateAuditorsView: Boolean,
                     number: String,
                     label: String,
                     IBAN: String,
                     generateAccountantsView: Boolean,
                     balance: Balance,
                     `type`: String,
                     id: String,
                     bank: String,
                     branchId: String,
                     accountRoutingAddress: String,
                     accountRoutingScheme: String
                   )
case class Customer(
                     name: String,
                     number: String
                   )
case class CrmEvents(
                      customer: Customer,
                      category: String,
                      actualDate: String,
                      detail: String,
                      bankId: String,
                      id: String,
                      channel: String
                    )
case class Banks(
                  website: String,
                  logo: String,
                  fullName: String,
                  shortName: String,
                  id: String
                )
case class Products(
                     category: String,
                     superFamily: String,
                     code: String,
                     name: String,
                     family: String,
                     meta: MetaBis,
                     bankId: String,
                     moreInfoUrl: String
                   )
case class Counterparties(
                           thisViewId: String,
                           otherBankRoutingScheme: String,
                           thisBankId: String,
                           thisAccountId: String,
                           otherBranchRoutingAddress: String,
                           otherAccountRoutingAddress: String,
                           otherBankRoutingAddress: String,
                           createdByUserId: String,
                           counterpartyId: String,
                           otherAccountRoutingScheme: String,
                           otherBranchRoutingScheme: String,
                           isBeneficiary: Boolean,
                           name: String
                         )
case class TransactionRequestTypes(
                                    chargeSummary: String,
                                    createDate: String,
                                    id: String,
                                    updateDate: String,
                                    bankId: String,
                                    chargeCurrency: String,
                                    chargeAmount: String,
                                    transactionRequestType: String
                                  )
case class Example(
                           branches: List[Branches],
                           users: String,
                           transactions: String,
                           fxRates: List[FxRates],
                           atms: List[Atms],
                           accounts: List[Accounts],
                           crmEvents: List[CrmEvents],
                           banks: List[Banks],
                           products: List[Products],
                           counterparties: List[Counterparties],
                           transactionRequestTypes: List[TransactionRequestTypes]
                         )