
package com.boot.ordercraft.service;

import com.boot.ordercraft.exception.DuplicateEmailException;
import com.boot.ordercraft.exception.DuplicatePhoneException;
import com.boot.ordercraft.model.Address;
import com.boot.ordercraft.model.Suppliers;
import com.boot.ordercraft.model.User;
import com.boot.ordercraft.repository.SuppliersRepository;
import com.boot.ordercraft.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.time.LocalDate;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;



@Service
public class SuppliersService {
	
	private static final String CONTRACTS_DIR = "contracts";

    @Autowired
    private SuppliersRepository suppliersRepository;

    @Autowired
    private UserRepository userRepository;

    public Suppliers saveSupplier(Suppliers supplier) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        // Check for duplicate email
        suppliersRepository.findByContactEmail(supplier.getContact_email())
                .ifPresent(existing -> {
                    throw new DuplicateEmailException("A supplier with this email already exists!");
                });

        // Check for duplicate phone
        suppliersRepository.findByPhone(supplier.getPhone())
                .ifPresent(existing -> {
                    throw new DuplicatePhoneException("A supplier with this phone already exists!");
                });

        supplier.setUser(existingUser);

        return suppliersRepository.save(supplier);
    }


    public List<Suppliers> getAllSuppliers() {
        return suppliersRepository.findAll();
    }
    
    public Suppliers updateSupplier(Long id, Suppliers updatedSupplier) {
        Suppliers existing = suppliersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));

        existing.setSupplier_name(updatedSupplier.getSupplier_name());
        existing.setContact_name(updatedSupplier.getContact_name());
        existing.setContact_email(updatedSupplier.getContact_email());
        existing.setPhone(updatedSupplier.getPhone());
        existing.setAddress(updatedSupplier.getAddress());
        existing.setRating(updatedSupplier.getRating());

        return suppliersRepository.save(existing);
    }

    public void deleteSupplier(Long id) {
        if (!suppliersRepository.existsById(id)) {
            throw new RuntimeException("Supplier not found with id: " + id);
        }
        suppliersRepository.deleteById(id);
    }
    
    public Suppliers getSupplierById(Long id) {
        return suppliersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
    }
    
    public void uploadContract(Long id, MultipartFile file) throws IOException {
        Suppliers supplier = suppliersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));

        // Ensure directory exists
        File dir = new File(CONTRACTS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Save file with supplierId in name
        String fileName = "supplier_" + id + "_contract.pdf";
        Path filePath = Paths.get(CONTRACTS_DIR, fileName);
        Files.write(filePath, file.getBytes());

        // Save path in DB
        supplier.setContractFile(filePath.toString());
        suppliersRepository.save(supplier);
    }

    public Resource downloadContract(Long id) {
        Suppliers supplier = suppliersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));

        if (supplier.getContractFile() == null) {
            throw new RuntimeException("No contract uploaded for supplier with id: " + id);
        }

        return new FileSystemResource(supplier.getContractFile());
    }

    
    public Resource generateContract(Long supplierId) throws IOException {
        Suppliers supplier = suppliersRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        String fileName = "contract_supplier_" + supplierId + ".pdf";
        String filePath = System.getProperty("java.io.tmpdir") + "/" + fileName;

        Document document = new Document(PageSize.A4, 50, 50, 50, 50); // margins
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);

            // Title
            Paragraph title = new Paragraph("SUPPLIER CONTRACT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Date
            Paragraph date = new Paragraph("Date: " + LocalDate.now(), normalFont);
            date.setAlignment(Element.ALIGN_RIGHT);
            date.setSpacingAfter(15);
            document.add(date);

            // Supplier Details Table
            Paragraph supplierDetailsHeader = new Paragraph("Supplier Details", sectionFont);
            supplierDetailsHeader.setSpacingAfter(10);
            document.add(supplierDetailsHeader);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(5);
            table.setSpacingAfter(15);

            table.addCell(getCell("Name:", PdfPCell.ALIGN_LEFT, sectionFont));
            table.addCell(getCell(supplier.getSupplier_name(), PdfPCell.ALIGN_LEFT, normalFont));
            table.addCell(getCell("Email:", PdfPCell.ALIGN_LEFT, sectionFont));
            table.addCell(getCell(supplier.getContact_email(), PdfPCell.ALIGN_LEFT, normalFont));
            table.addCell(getCell("Phone:", PdfPCell.ALIGN_LEFT, sectionFont));
            table.addCell(getCell(supplier.getPhone(), PdfPCell.ALIGN_LEFT, normalFont));
            table.addCell(getCell("Address:", PdfPCell.ALIGN_LEFT, sectionFont));

            Address addr = supplier.getAddress(); // assuming Address has getStreet, getCity, getState, getZipCode
            String addressString = addr.getAddressStreet() + ", " + addr.getAddressCity() + ", " + addr.getAddressState() + " - " + addr.getAddressPostalCode();
            table.addCell(getCell(addressString, PdfPCell.ALIGN_LEFT, normalFont));


            document.add(table);

            // Terms & Conditions
            Paragraph termsHeader = new Paragraph("Terms & Conditions", sectionFont);
            termsHeader.setSpacingAfter(10);
            document.add(termsHeader);

            com.itextpdf.text.List terms = new com.itextpdf.text.List(com.itextpdf.text.List.ORDERED);
            terms.add(new ListItem("Supplier agrees to provide quality goods as per agreed terms.", normalFont));
            terms.add(new ListItem("Payment will be made within 30 days of delivery.", normalFont));
            terms.add(new ListItem("Any disputes will be resolved under Indian jurisdiction.", normalFont));
            terms.add(new ListItem("This contract is valid for 1 year from the date of signing.", normalFont));

            document.add(terms);

            // Signature Lines
            Paragraph spacing = new Paragraph("\n\n", normalFont);
            document.add(spacing);

            PdfPTable sigTable = new PdfPTable(2);
            sigTable.setWidthPercentage(100);
            sigTable.setSpacingBefore(30);

            sigTable.addCell(getSignatureCell("Authorized Signatory"));
            sigTable.addCell(getSignatureCell("Supplier Signature"));

            document.add(sigTable);

        } catch (DocumentException e) {
            throw new IOException("Error while creating PDF contract", e);
        } finally {
            document.close();
        }

        return new FileSystemResource(new File(filePath));
    }

    // Helper method to create table cells
    private PdfPCell getCell(String text, int alignment, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    // Helper method for signature cells
    private PdfPCell getSignatureCell(String title) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setPaddingTop(30);
        Paragraph p = new Paragraph("__________________________\n" + title, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11));
        p.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(p);
        return cell;
    }


}
