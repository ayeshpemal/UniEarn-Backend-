package com.finalproject.uni_earn.entity.enums;

public enum ReportType {
    /*
     * penalty: 8
     * - Sexual Harassment
     * - Verbal Abuse
     * - Threatening Behavior
     * - Discriminatory Behavior (based on race, gender, etc.)
     * - Unsafe Working Conditions
     * - Stalking or Unwanted Contact
     */
    Harassment_and_Safety_Issues,

    /*
     * penalty: 7
     * - Non-payment for Work
     * - Delayed Payments
     * - Payment Amount Disputes
     * - False Payment Proof
     * - Unauthorized Bank Details Requests
     * - Hidden Fees or Charges
     */
    Fraud_and_Payment_Issues,

    /*
     * penalty: 6
     * - Inappropriate Images/Videos
     * - Offensive Language
     * - Adult Content
     * - Hate Speech
     * - Violence
     * - Gore or Disturbing Content
     */
    Inappropriate_Content,

    /*
     * penalty: 5
     * - Fake Company/Business
     * - False University Affiliation
     * - Impersonating Another User
     * - Using Someone Else's Documents
     * - False Credentials
     * - Fake Contact Information
     */
    Identity_Misrepresentation,

    /*
     * penalty: 4
     * - False Job Description
     * - Hidden Job Requirements
     * - Misleading Salary Information
     * - Inaccurate Working Hours
     * - Wrong Job Location
     * - Different Job Role Than Advertised
     */
    Job_Misrepresentation,

    /*
     * penalty: 3
     * - No-show at Work
     * - Unprofessional Communication
     * - Excessive Personal Questions
     * - Poor Time Management
     * - Breach of Confidentiality
     * - Unauthorized Sharing of Information
     */
    Professional_Conduct_Issues,

    /*
     * penalty: 2
     * - Miscommunication
     * - Schedule Conflicts
     * - Task Completion Disagreements
     * - Late Responses
     * - Work Style Differences
     */
    Work_Environment_Concerns,

    /*
     * penalty: Base 1
     * For any issues that don't fit into above categories
     */
    Other
}