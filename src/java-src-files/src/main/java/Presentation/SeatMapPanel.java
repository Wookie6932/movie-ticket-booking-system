package Presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import Movie.Showtime;
import Reservation.ReservationService;
import Reservation.ShowtimeSeat;

public class SeatMapPanel extends JPanel {
    
    private static final Color AVAILABLE_COLOR = Color.GREEN;
    private static final Color TAKEN_COLOR = Color.RED;
    private static final Color SELECTED_COLOR = Color.GRAY;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");

    private final CustomerDashboard dashboard;
    private final ReservationService reservationService;

    private Showtime currentShowtime;
    private final List<ShowtimeSeat> selectedSeats = new ArrayList<>();

    private final JLabel titleLabel = new JLabel("Select Your Seats");
    private final JLabel messageLabel = new JLabel(" ");
    private final JPanel gridPanel = new JPanel(new GridBagLayout());
    private final JButton continueButton = new JButton("Continue");

    public SeatMapPanel(CustomerDashboard dashboard, ReservationService reservationService) {
        this.dashboard = dashboard;
        this.reservationService = reservationService;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);
 
        JLabel screenLabel = new JLabel("SCREEN", SwingConstants.CENTER);
        screenLabel.setOpaque(true);
        screenLabel.setBackground(Color.LIGHT_GRAY);
        screenLabel.setFont(new Font("Arial", Font.PLAIN, 14));
 
        gridPanel.setBackground(Color.WHITE);
 
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(screenLabel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(gridPanel), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
 
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        legendPanel.setBackground(Color.WHITE);
        legendPanel.add(createLegendLabel("Available", AVAILABLE_COLOR));
        legendPanel.add(createLegendLabel("Selected", SELECTED_COLOR));
        legendPanel.add(createLegendLabel("Taken", TAKEN_COLOR));
 
        continueButton.setEnabled(false);
        continueButton.addActionListener(e -> dashboard.showSummary(currentShowtime, new ArrayList<>(selectedSeats)));
 
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 8));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(legendPanel, BorderLayout.NORTH);
        bottomPanel.add(messageLabel, BorderLayout.CENTER);
        bottomPanel.add(continueButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void showSeatsFor(Showtime showtime) {
        currentShowtime = showtime;
        selectedSeats.clear();
 
        titleLabel.setText("Select Seats - " + showtime.getMovieTitle() + ", " + showtime.getStartTime().format(FORMATTER));
        messageLabel.setText("Loading seats...");
        continueButton.setEnabled(false);
        gridPanel.removeAll();
        gridPanel.revalidate();
        gridPanel.repaint();
 
        new SwingWorker<List<ShowtimeSeat>, Void>() {
            @Override
            protected List<ShowtimeSeat> doInBackground() throws Exception {
                return reservationService.getSeatsForShowtime(showtime.getShowtimeId());
            }
 
            @Override
            protected void done() {
                if (showtime != currentShowtime) {
                    return;
                }
 
                try {
                    List<ShowtimeSeat> seats = get();
                    
                    if (seats.isEmpty()) {
                        messageLabel.setText("No seats have been set up for this showtime.");
                    } else {
                        buildGrid(seats);
                        messageLabel.setText("Click seats to select them.");
                    }
 
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    messageLabel.setText("Unable to load seats. Please try again.");
 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }

    private void buildGrid(List<ShowtimeSeat> seats) {
        gridPanel.removeAll();
 
        Map<String, List<ShowtimeSeat>> rows = new LinkedHashMap<>();
 
        for (ShowtimeSeat seat : seats) {
            rows.computeIfAbsent(seat.getRowLabel(), key -> new ArrayList<>()).add(seat);
        }
 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
 
        int gridY = 0;
 
        for (Map.Entry<String, List<ShowtimeSeat>> row : rows.entrySet()) {
            gbc.gridy = gridY++;
            gbc.gridx = 0;
            gridPanel.add(new JLabel(row.getKey()), gbc);
 
            int gridX = 1;
 
            for (ShowtimeSeat seat : row.getValue()) {
                gbc.gridx = gridX++;
                gridPanel.add(createSeatButton(seat), gbc);
            }
        }
 
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private JButton createSeatButton(ShowtimeSeat seat) {
        JButton button = new JButton(String.valueOf(seat.getSeatNumber()));
        button.setPreferredSize(new Dimension(42, 32));
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setToolTipText("Seat " + seat.getSeatLabel());
 
        if (seat.isAvailable()) {
            button.setBackground(AVAILABLE_COLOR);
            button.addActionListener(e -> toggleSeat(seat, button));
        } else {
            button.setBackground(TAKEN_COLOR);
            button.setEnabled(false);
        }
 
        return button;
    }

    private void toggleSeat(ShowtimeSeat seat, JButton button) {
        if (selectedSeats.contains(seat)) {
            selectedSeats.remove(seat);
            button.setBackground(AVAILABLE_COLOR);
        } else {
            selectedSeats.add(seat);
            button.setBackground(SELECTED_COLOR);
        }
 
        updateSelectionMessage();
    }

    private void updateSelectionMessage() {
        if (selectedSeats.isEmpty()) {
            messageLabel.setText("Click seats to select them.");
            continueButton.setEnabled(false);
            return;
        }
 
        StringBuilder labels = new StringBuilder();
 
        for (ShowtimeSeat seat : selectedSeats) {
            if (labels.length() > 0) {
                labels.append(", ");
            }
            labels.append(seat.getSeatLabel());
        }
 
        messageLabel.setText("Selected: " + labels);
        continueButton.setEnabled(true);
    }

    private JLabel createLegendLabel(String text, Color color) {
        JLabel label = new JLabel(" " + text + " ");
        label.setOpaque(true);
        label.setBackground(color);
        return label;
    }
}