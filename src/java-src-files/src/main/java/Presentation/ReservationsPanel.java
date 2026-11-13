package Presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import Account.User;
import Reservation.ReservationRecord;
import Reservation.ReservationService;

public class ReservationsPanel extends JPanel {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");

    private final ReservationService reservationService;
    private final User currentUser;

    private final DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Movie", "Showtime", "Auditorium", "Seats", "Booked On"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    private final List<ReservationRecord> reservations = new ArrayList<>();

    private final JLabel messageLabel = new JLabel(" ");
    private final JButton cancelButton = new JButton("Cancel Selected Reservation");
    private final JButton refreshButton = new JButton("Refresh");

    public ReservationsPanel(ReservationService reservationService, User currentUser) {
        this.reservationService = reservationService;
        this.currentUser = currentUser;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("My Reservations");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        cancelButton.addActionListener(e -> cancelSelectedReservation());
        refreshButton.addActionListener(e -> loadReservations());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(cancelButton);
        buttonPanel.add(refreshButton);

        JPanel bottomPanel = new JPanel(new BorderLayout(0, 8));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(messageLabel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void loadReservations() {
        messageLabel.setText("Loading reservations");
        setButtonsEnabled(false);

        new SwingWorker<List<ReservationRecord>, Void>() {

            @Override
            protected List<ReservationRecord> doInBackground() throws Exception {
                return reservationService.getActiveReservations(currentUser.getUserId());
            }

            @Override
            protected void done() {
                try {
                    List<ReservationRecord> loaded = get();

                    tableModel.setRowCount(0);
                    reservations.clear();

                    for (ReservationRecord record : loaded) {
                        reservations.add(record);
                        tableModel.addRow(new Object[]{
                                record.getMovieTitle(),
                                record.getShowtimeStart().format(FORMATTER),
                                record.getAuditoriumId(),
                                record.getSeatLabels(),
                                record.getReservationDate().format(FORMATTER)
                        });
                    }
                    messageLabel.setText(loaded.isEmpty() ? "You have no active reservations." : " ");

                } catch (ExecutionException e) {
                    e.printStackTrace();
                    messageLabel.setText("Unable to load reservations. Please try again.");

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();

                } finally {
                    setButtonsEnabled(true);
                }
            }

        }.execute();
    }

    private void cancelSelectedReservation() {
        int row = table.getSelectedRow();

        if (row < 0 || row >= reservations.size()) {
            JOptionPane.showMessageDialog(this, "Select a reservation to cancel.");
            return;
        }

        final ReservationRecord selected = reservations.get(row);

        int confirm = JOptionPane.showConfirmDialog(this, "Cancel your reservation for \"" + selected.getMovieTitle()
                + "\" on " + selected.getShowtimeStart().format(FORMATTER) + "?\n\nSeats: " + selected.getSeatLabels(), 
                "Confirm Cancel", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        setButtonsEnabled(false);

        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                reservationService.cancelReservation(selected.getReservationId(), currentUser.getUserId());
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();

                    JOptionPane.showMessageDialog(ReservationsPanel.this, "Reservation canceled. Your seats have been released.",
                            "Reservation Canceled", JOptionPane.INFORMATION_MESSAGE);

                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();

                    if (cause instanceof IllegalStateException || cause instanceof IllegalArgumentException) {
                        JOptionPane.showMessageDialog(ReservationsPanel.this, cause.getMessage(), "Unable to Cancel", JOptionPane.WARNING_MESSAGE);
                    } else {
                        cause.printStackTrace();
                        JOptionPane.showMessageDialog(ReservationsPanel.this,
                                "Unable to cancel the reservation. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();

                } finally {
                    loadReservations();
                }
            }

        }.execute();
    }

    private void setButtonsEnabled(boolean enabled) {
        cancelButton.setEnabled(enabled);
        refreshButton.setEnabled(enabled);
    }
}