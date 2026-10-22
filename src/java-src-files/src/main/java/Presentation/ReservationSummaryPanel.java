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
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import Account.User;
import Movie.Showtime;
import Reservation.ReservationService;
import Reservation.SeatUnavailableException;
import Reservation.ShowtimeSeat;

public class ReservationSummaryPanel extends JPanel {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");
 
    private final CustomerDashboard dashboard;
    private final ReservationService reservationService;
    private final User currentUser;
 
    private Showtime showtime;
    private List<ShowtimeSeat> seats = new ArrayList<>();
 
    private final JTextArea summaryArea = new JTextArea();
    private final JButton confirmButton = new JButton("Confirm Reservation");
    private final JButton backButton = new JButton("Back");
 
    public ReservationSummaryPanel(CustomerDashboard dashboard, ReservationService reservationService, User currentUser) {
        this.dashboard = dashboard;
        this.reservationService = reservationService;
        this.currentUser = currentUser;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
 
        JLabel titleLabel = new JLabel("Reservation Summary");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);
 
        summaryArea.setFont(new Font("Arial", Font.PLAIN, 16));
        summaryArea.setEditable(false);
        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);
        add(summaryArea, BorderLayout.CENTER);
 
        confirmButton.addActionListener(e -> confirmReservation());
        backButton.addActionListener(e -> dashboard.showSeatMap(showtime));
 
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(confirmButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void showSummary(Showtime showtime, List<ShowtimeSeat> seats) {
        this.showtime = showtime;
        this.seats = seats;
 
        StringBuilder labels = new StringBuilder();
 
        for (ShowtimeSeat seat : seats) {
            if (labels.length() > 0) {
                labels.append(", ");
            }
            labels.append(seat.getSeatLabel());
        }
 
        summaryArea.setText("Movie: " + showtime.getMovieTitle() + "\n\n"
                + "Showtime: " + showtime.getStartTime().format(FORMATTER) + "\n\n"
                + "Auditorium: " + showtime.getAuditoriumId() + "\n\n"
                + "Seats (" + seats.size() + "): " + labels);
    }

    private void confirmReservation() {
        final Showtime selectedShowtime = showtime;
        final List<Integer> seatIds = new ArrayList<>();
 
        for (ShowtimeSeat seat : seats) {
            seatIds.add(seat.getShowtimeSeatId());
        }
 
        confirmButton.setEnabled(false);
        backButton.setEnabled(false);
 
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                reservationService.createReservation(currentUser.getUserId(), selectedShowtime.getShowtimeId(), seatIds);
                return null;
            }
 
            @Override
            protected void done() {
                try {
                    get();
 
                    JOptionPane.showMessageDialog(ReservationSummaryPanel.this, 
                            "Reservation confirmed.", "Reservation Confirmed", JOptionPane.INFORMATION_MESSAGE);
 
                    dashboard.showSeatMap(selectedShowtime);
 
                } catch (ExecutionException e) {
                    handleFailure(e.getCause(), selectedShowtime);
 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
 
                } finally {
                    confirmButton.setEnabled(true);
                    backButton.setEnabled(true);
                }
            }
        }.execute();
    }

    private void handleFailure(Throwable cause, Showtime selectedShowtime) {
        if (cause instanceof SeatUnavailableException) {
            JOptionPane.showMessageDialog(this, "One or more of the selected seats have already been reserved. " 
                    + "Please choose another seat.", "Seats Unavailable", JOptionPane.WARNING_MESSAGE);
            dashboard.showSeatMap(selectedShowtime);
 
        } else if (cause instanceof IllegalArgumentException
                || cause instanceof IllegalStateException) {
            JOptionPane.showMessageDialog(this, cause.getMessage(), "Unable to Reserve", JOptionPane.WARNING_MESSAGE);
            dashboard.showSeatMap(selectedShowtime);
 
        } else {
            cause.printStackTrace();
            JOptionPane.showMessageDialog(this, "Unable to complete the reservation. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}